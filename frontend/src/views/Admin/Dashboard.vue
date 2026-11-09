<template>
  <div>
    <h1>数据概览</h1>
    <div class="stats-grid">
      <div class="stat-card"><strong>{{ stats.total ?? 0 }}</strong><span>询单总数</span></div>
      <div class="stat-card"><strong>{{ stats.newThisMonth ?? 0 }}</strong><span>本月新增</span></div>
      <div class="stat-card sales-only" v-if="isAdmin"><strong>{{ salesCount }}</strong><span>我负责的</span></div>
    </div>

    <div class="chart-section" v-if="isAdmin">
      <div class="chart-head">
        <h2 class="chart-title">
          询单统计
          <select v-model.number="selectedYear" class="month-select">
            <option v-for="y in availableYears" :key="y" :value="y">{{ y }}年</option>
          </select>
          <select v-model.number="selectedMonth" class="month-select">
            <option v-for="m in monthOptions" :key="m" :value="m">{{ m }}月</option>
          </select>
        </h2>
        <div class="chart-head-right">
          <span class="chart-sub">按日期统计，共 {{ currentMonth.total ?? 0 }} 条</span>
          <div class="chart-toggle">
            <button :class="{ active: chartType === 'line' }" @click="chartType = 'line'">折线</button>
            <button :class="{ active: chartType === 'bar' }" @click="chartType = 'bar'">柱状</button>
          </div>
        </div>
      </div>
      <div class="trend-row">
        <div class="trend-card">
          <span class="trend-label">{{ selectedMonth }} 月询单</span>
          <strong class="trend-value">{{ currentMonth.total ?? 0 }}</strong>
        </div>
        <div class="trend-card">
          <span class="trend-label">环比上月</span>
          <strong class="trend-value" :class="trendClass">{{ trendText }}</strong>
          <small class="trend-hint">上月 {{ currentMonth.previousTotal ?? 0 }} 条</small>
        </div>
        <div class="trend-card">
          <span class="trend-label">日均询单</span>
          <strong class="trend-value">{{ currentMonth.dailyAverage ?? 0 }}</strong>
          <small class="trend-hint">已过 {{ currentMonth.elapsedDays ?? 0 }} 天</small>
        </div>
      </div>
      <div v-if="chartType === 'line'" class="line-chart">
        <svg class="line-svg" viewBox="0 0 1000 100" preserveAspectRatio="none" aria-hidden="true">
          <defs>
            <linearGradient id="lineAreaGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stop-color="#8B1A1A" stop-opacity="0.22" />
              <stop offset="100%" stop-color="#8B1A1A" stop-opacity="0" />
            </linearGradient>
          </defs>
          <path :d="areaPath" fill="url(#lineAreaGradient)" />
          <polyline
            :points="linePoints"
            fill="none"
            stroke="#8B1A1A"
            stroke-width="2"
            vector-effect="non-scaling-stroke"
          />
        </svg>
        <div class="line-points">
          <span
            v-for="(p, i) in points"
            :key="i"
            class="line-point"
            :style="{ left: p.leftPercent + '%', bottom: p.bottomPercent + '%' }"
            :title="`${i + 1}日：${daily[i] ?? 0} 条`"
          />
        </div>
        <div class="line-labels">
          <span
            v-for="(item, i) in points"
            :key="i"
            class="line-label"
            :style="{ left: item.leftPercent + '%' }"
          >{{ showDayLabel(i) ? i + 1 : '' }}</span>
        </div>
      </div>
      <div v-else class="daily-chart" :style="{ height: dailyChartHeight + 'px' }">
        <div v-for="(count, i) in daily" :key="i" class="day-bar-wrap">
          <span class="day-bar-value">{{ count > 0 ? count : '' }}</span>
          <div
            class="day-bar"
            :class="{ empty: count === 0 }"
            :style="{ height: barHeight(count) + 'px' }"
            :title="`${i + 1}日：${count} 条`"
          />
          <span class="day-label">{{ showDayLabel(i) ? i + 1 : '' }}</span>
        </div>
      </div>
    </div>

    <div class="recent-section">
      <h2>最近询单</h2>
      <table class="data-table">
        <thead><tr><th>日期</th><th>姓名</th><th>邮箱</th><th>国家/地区</th><th>状态</th><th v-if="isAdmin">分配给</th></tr></thead>
        <tbody>
          <tr v-for="q in inquiries.slice(0, 8)" :key="q.id">
            <td>{{ formatDate(q.createTime) }}</td>
            <td>{{ q.name }}</td>
            <td>{{ q.email }}</td>
            <td>{{ q.country || '—' }}</td>
            <td><span class="badge" :class="q.status">{{ statusLabel(q.status) }}</span></td>
            <td v-if="isAdmin">{{ q.assignedTo ? '是' : '—' }}</td>
          </tr>
          <tr v-if="!inquiries.length"><td colspan="6" class="empty">暂无询单</td></tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { api } from '../../api'

const isAdmin = computed(() => localStorage.getItem('yqmetals_role') === 'ADMIN')
const stats = ref<any>({})
const inquiries = ref<any[]>([])
const salesCount = ref(0)

const currentMonth = ref<any>({ daily: [] })
const chartType = ref<'line' | 'bar'>('line')

const now = new Date()
const selectedYear = ref(now.getFullYear())
const selectedMonth = ref(now.getMonth() + 1)
const availableYears = ref<number[]>([now.getFullYear()])
const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1)

const DAY_BAR_UNIT = 24
const MAX_DAY_BAR = 200
const DAY_LABEL_SPACE = 48
const LINE_BOTTOM = 8
const LINE_TOP = 92

const daily = computed(() => currentMonth.value.daily || [])

const points = computed(() => {
  const data = daily.value
  if (!data.length) return []
  const max = Math.max(...data, 1)
  return data.map((count: number, i: number) => ({
    leftPercent: data.length === 1 ? 50 : (i / (data.length - 1)) * 100,
    bottomPercent: LINE_BOTTOM + (count / max) * (LINE_TOP - LINE_BOTTOM),
  }))
})

const linePoints = computed(() =>
  points.value.map((p: any) => `${p.leftPercent * 10},${100 - p.bottomPercent}`).join(' ')
)

const areaPath = computed(() => {
  const pts = points.value
  if (!pts.length) return ''
  const coords = pts.map((p: any) => ({ x: p.leftPercent * 10, y: 100 - p.bottomPercent }))
  let d = `M 0,100 L ${coords[0].x},${coords[0].y}`
  for (let i = 1; i < coords.length; i++) {
    d += ` L ${coords[i].x},${coords[i].y}`
  }
  d += ' L 1000,100 Z'
  return d
})

const barHeight = (count: number) => (count > 0 ? Math.min(count * DAY_BAR_UNIT, MAX_DAY_BAR) : 2)

const dailyChartHeight = computed(() => {
  const max = Math.max(...daily.value, 1)
  return Math.min(max * DAY_BAR_UNIT, MAX_DAY_BAR) + DAY_LABEL_SPACE
})

const trendText = computed(() => {
  const p = currentMonth.value.changePercent
  if (p === null || p === undefined) {
    return (currentMonth.value.total ?? 0) > 0 ? '本月新增' : '—'
  }
  if (p > 0) return `↑ ${p}%`
  if (p < 0) return `↓ ${Math.abs(p)}%`
  return '— 持平'
})

const trendClass = computed(() => {
  const p = currentMonth.value.changePercent
  if (p === null || p === undefined) {
    return (currentMonth.value.total ?? 0) > 0 ? 'up' : 'flat'
  }
  return p > 0 ? 'up' : p < 0 ? 'down' : 'flat'
})

const showDayLabel = (i: number) => {
  const day = i + 1
  const total = currentMonth.value.daysInMonth || 31
  return day === 1 || day % 5 === 0 || day === total
}

async function loadMonthStats() {
  try {
    currentMonth.value = await api.inquiries.currentMonthStats(selectedYear.value, selectedMonth.value)
  } catch {
    currentMonth.value = { daily: [] }
  }
}

watch([selectedYear, selectedMonth], () => {
  if (isAdmin.value) loadMonthStats()
})

async function load() {
  if (isAdmin.value) {
    stats.value = await api.inquiries.stats()
    try {
      availableYears.value = await api.inquiries.availableYears()
    } catch {
      availableYears.value = [now.getFullYear()]
    }
    await loadMonthStats()
    inquiries.value = await api.inquiries.getAll()
  } else {
    inquiries.value = await api.inquiries.getMine()
    salesCount.value = inquiries.value.length
  }
}

function formatDate(d: string) { return new Date(d).toLocaleDateString('zh-CN', { year: 'numeric', month: 'short', day: 'numeric' }) }
function statusLabel(s: string) {
  const map: Record<string, string> = { NEW: '新询单', ASSIGNED: '已分配', IN_PROGRESS: '处理中', CLOSED: '已关闭' }
  return map[s] || s
}
onMounted(load)
</script>

<style scoped>
h1 { font-size: 22px; margin-bottom: 20px; }
h2 { font-size: 16px; margin-bottom: 20px; color: var(--yq-gray-800); }
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 32px; }
.stat-card { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 20px; text-align: center; }
.stat-card.sales-only { border-left: 3px solid var(--yq-red); }
.stat-card strong { display: block; font-size: 32px; color: var(--yq-red); }
.stat-card span { font-size: 12px; color: #888; text-transform: uppercase; }
.chart-section { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 20px; margin-bottom: 24px; }
.chart-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 16px; }
.chart-head h2 { margin-bottom: 0; }
.chart-title { display: flex; align-items: center; gap: 8px; }
.month-select { padding: 4px 8px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-family: var(--font-sans); font-size: 13px; color: var(--yq-gray-800); background: #fff; cursor: pointer; }
.month-select:focus { outline: none; border-color: var(--yq-red); }
.chart-sub { font-size: 12px; color: #999; }
.chart-head-right { display: flex; align-items: center; gap: 16px; }
.chart-toggle { display: inline-flex; border: 1px solid var(--yq-gray-200); border-radius: 4px; overflow: hidden; }
.chart-toggle button { padding: 5px 14px; border: none; background: #fff; font-size: 12px; color: #666; cursor: pointer; }
.chart-toggle button + button { border-left: 1px solid var(--yq-gray-200); }
.chart-toggle button.active { background: var(--yq-red); color: #fff; }
.trend-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 24px; }
.trend-card { background: var(--yq-gray-50); border: 1px solid var(--yq-gray-100); border-radius: 6px; padding: 14px 16px; }
.trend-label { font-size: 11px; color: #888; text-transform: uppercase; letter-spacing: 0.06em; }
.trend-value { display: block; margin-top: 4px; font-size: 26px; color: var(--yq-gray-900); }
.trend-value.up { color: #1a7f37; }
.trend-value.down { color: #c0392b; }
.trend-value.flat { color: #888; }
.trend-hint { display: block; margin-top: 2px; font-size: 11px; color: #999; }
.daily-chart { display: flex; align-items: flex-end; gap: 3px; padding-top: 8px; }
.line-chart { position: relative; height: 220px; padding-bottom: 24px; }
.line-svg { position: absolute; left: 0; right: 0; top: 0; height: calc(100% - 24px); width: 100%; }
.line-points { position: absolute; left: 0; right: 0; top: 0; height: calc(100% - 24px); }
.line-point { position: absolute; width: 6px; height: 6px; border-radius: 50%; background: var(--yq-red); transform: translate(-50%, 50%); box-shadow: 0 0 0 2px #fff; }
.line-labels { position: absolute; left: 0; right: 0; bottom: 0; height: 20px; }
.line-label { position: absolute; transform: translateX(-50%); font-size: 9px; color: #999; white-space: nowrap; }
.day-bar-wrap { flex: 1; height: 100%; min-width: 0; display: flex; flex-direction: column; align-items: center; justify-content: flex-end; gap: 3px; }
.day-bar { width: 100%; max-width: 18px; background: var(--yq-red); border-radius: 2px 2px 0 0; min-height: 2px; transition: height 0.3s; }
.day-bar.empty { background: var(--yq-gray-200); }
.day-bar-value { font-size: 9px; font-weight: 600; color: var(--yq-gray-600); }
.day-label { font-size: 9px; color: #999; white-space: nowrap; }
.recent-section { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 20px; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.data-table th { text-align: left; padding: 8px; border-bottom: 2px solid var(--yq-gray-200); font-size: 12px; color: #888; }
.data-table td { padding: 10px 8px; border-bottom: 1px solid var(--yq-gray-100); }
.badge { padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: 600; }
.badge.NEW { background: #fef3cd; color: #856404; }
.badge.ASSIGNED { background: #d1ecf1; color: #0c5460; }
.badge.IN_PROGRESS { background: #d4edda; color: #155724; }
.badge.CLOSED { background: var(--yq-gray-200); color: #666; }
.empty { text-align: center; color: #999; padding: 20px !important; }
@media (max-width: 600px) { .stats-grid { grid-template-columns: 1fr; } }
</style>
