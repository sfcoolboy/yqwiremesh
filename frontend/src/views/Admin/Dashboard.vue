<template>
  <div>
    <h1>Dashboard</h1>
    <div class="stats-grid">
      <div class="stat-card"><strong>{{ stats.total ?? 0 }}</strong><span>Total Inquiries</span></div>
      <div class="stat-card"><strong>{{ stats.newThisMonth ?? 0 }}</strong><span>New This Month</span></div>
      <div class="stat-card sales-only" v-if="isAdmin"><strong>{{ salesCount }}</strong><span>My Assigned</span></div>
    </div>

    <div class="chart-section" v-if="isAdmin">
      <h2>Monthly Inquiries</h2>
      <div class="chart">
        <div v-for="item in chartData" :key="item.month" class="bar-wrap">
          <div class="bar" :style="{ height: item.percent + 'px' }" :title="`${item.month}: ${item.count}`" />
          <span class="bar-label">{{ item.monthLabel }}</span>
          <span class="bar-value">{{ item.count }}</span>
        </div>
      </div>
    </div>

    <div class="recent-section">
      <h2>Recent Inquiries</h2>
      <table class="data-table">
        <thead><tr><th>Date</th><th>Name</th><th>Email</th><th>Country</th><th>Status</th><th>Assigned</th></tr></thead>
        <tbody>
          <tr v-for="q in inquiries.slice(0, 8)" :key="q.id">
            <td>{{ formatDate(q.createTime) }}</td>
            <td>{{ q.name }}</td>
            <td>{{ q.email }}</td>
            <td>{{ q.country || '—' }}</td>
            <td><span class="badge" :class="q.status">{{ q.status }}</span></td>
            <td>{{ q.assignedTo ? 'Yes' : '—' }}</td>
          </tr>
          <tr v-if="!inquiries.length"><td colspan="6" class="empty">No inquiries yet</td></tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { api } from '../../api'

const isAdmin = computed(() => localStorage.getItem('yqmetals_role') === 'ADMIN')
const stats = ref<any>({})
const inquiries = ref<any[]>([])
const salesCount = ref(0)

const chartData = computed(() => {
  const monthly = stats.value.monthly || {}
  const entries = Object.entries(monthly).sort((a: any, b: any) => a[0].localeCompare(b[0]))
  const max = Math.max(...entries.map(([, v]: any) => v as number), 1)
  return entries.map(([month, count]: [string, number]) => ({
    month,
    monthLabel: month.slice(5),
    count,
    percent: (count / max) * 120,
  })).reverse()
})

async function load() {
  if (isAdmin.value) {
    stats.value = await api.inquiries.stats()
    inquiries.value = await api.inquiries.getAll()
  } else {
    inquiries.value = await api.inquiries.getMine()
    salesCount.value = inquiries.value.length
  }
}

function formatDate(d: string) { return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }) }

onMounted(load)
</script>

<style scoped>
h1 { font-size: 22px; margin-bottom: 20px; }
h2 { font-size: 16px; margin-bottom: 12px; color: var(--yq-gray-800); }
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 32px; }
.stat-card { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 20px; text-align: center; }
.stat-card.sales-only { border-left: 3px solid var(--yq-red); }
.stat-card strong { display: block; font-size: 32px; color: var(--yq-red); }
.stat-card span { font-size: 12px; color: #888; text-transform: uppercase; }
.chart-section { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 20px; margin-bottom: 24px; }
.chart { display: flex; align-items: flex-end; gap: 8px; height: 140px; }
.bar-wrap { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 4px; }
.bar { width: 100%; max-width: 40px; background: var(--yq-red); border-radius: 2px 2px 0 0; min-height: 2px; transition: height 0.3s; }
.bar-label { font-size: 10px; color: #888; }
.bar-value { font-size: 10px; font-weight: 600; }
.recent-section { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 20px; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.data-table th { text-align: left; padding: 8px; border-bottom: 2px solid var(--yq-gray-200); font-size: 12px; color: #888; text-transform: uppercase; }
.data-table td { padding: 10px 8px; border-bottom: 1px solid var(--yq-gray-100); }
.badge { padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: 600; }
.badge.NEW { background: #fef3cd; color: #856404; }
.badge.ASSIGNED { background: #d1ecf1; color: #0c5460; }
.badge.IN_PROGRESS { background: #d4edda; color: #155724; }
.badge.CLOSED { background: var(--yq-gray-200); color: #666; }
.empty { text-align: center; color: #999; padding: 20px !important; }
@media (max-width: 600px) { .stats-grid { grid-template-columns: 1fr; } }
</style>
