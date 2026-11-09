<template>
  <div>
    <div class="header">
      <h1>系统日志</h1>
      <span class="hint">记录登录与后台操作，仅管理员可见</span>
    </div>
    <div class="toolbar">
      <input v-model="filterUsername" class="filter-input" placeholder="按用户名筛选" @keyup.enter="search" />
      <select v-model="filterAction" class="filter-select">
        <option value="">全部操作</option>
        <option v-for="a in actionOptions" :key="a" :value="a">{{ a }}</option>
      </select>
      <button class="btn-query" @click="search">查询</button>
      <button class="btn-reset" @click="reset">重置</button>
    </div>
    <table class="data-table">
      <thead>
        <tr><th>时间</th><th>用户</th><th>操作</th><th>详情</th><th>IP</th><th>结果</th></tr>
      </thead>
      <tbody>
        <tr v-for="log in logs" :key="log.id">
          <td class="nowrap">{{ formatDateTime(log.createTime) }}</td>
          <td>{{ log.username || '—' }}</td>
          <td class="nowrap">{{ log.action }}</td>
          <td class="detail-cell" :title="log.detail">{{ log.detail || '—' }}</td>
          <td class="nowrap">{{ displayIp(log.ip) }}</td>
          <td>
            <span class="badge" :class="log.success ? 'ok' : 'fail'">{{ log.success ? '成功' : '失败' }}</span>
          </td>
        </tr>
        <tr v-if="!logs.length"><td colspan="6" class="empty">暂无日志</td></tr>
      </tbody>
    </table>
    <div class="pagination">
      <button :disabled="page <= 1" @click="prevPage">上一页</button>
      <span>第 {{ page }} / {{ pages }} 页 · 共 {{ total }} 条</span>
      <button :disabled="page >= pages" @click="nextPage">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../api'

const logs = ref<any[]>([])
const page = ref(1)
const size = 20
const total = ref(0)
const pages = ref(1)
const filterUsername = ref('')
const filterAction = ref('')

const actionOptions = [
  '登录',
  '分配询单', '重新分配询单',
  '创建用户', '更新用户', '删除用户', '重置用户密码',
  '创建产品', '更新产品', '删除产品',
  '创建类目', '更新类目', '删除类目',
  '创建产品参数', '更新产品参数', '删除产品参数', '分配产品参数',
  '启用产品参数', '停用产品参数',
  '创建新闻', '更新新闻', '删除新闻',
  '创建工厂图片', '更新工厂图片', '删除工厂图片',
  '上传图片',
  '更新系统设置',
]

async function load() {
  try {
    const res = await api.auditLogs.list({
      page: page.value,
      size,
      username: filterUsername.value,
      action: filterAction.value,
    })
    logs.value = res.records || []
    total.value = res.total || 0
    pages.value = res.pages || 1
  } catch {
    logs.value = []
    total.value = 0
    pages.value = 1
  }
}

function search() {
  page.value = 1
  load()
}

function reset() {
  filterUsername.value = ''
  filterAction.value = ''
  page.value = 1
  load()
}

function prevPage() {
  if (page.value > 1) {
    page.value--
    load()
  }
}

function nextPage() {
  if (page.value < pages.value) {
    page.value++
    load()
  }
}

function formatDateTime(d: string) {
  if (!d) return '—'
  return new Date(d).toLocaleString('zh-CN', { hour12: false })
}

function displayIp(ip: string) {
  if (!ip) return '—'
  if (ip === '0:0:0:0:0:0:0:1' || ip === '::1') return '127.0.0.1'
  return ip
}

onMounted(load)
</script>

<style scoped>
.header { display: flex; align-items: baseline; gap: 12px; margin-bottom: 16px; }
h1 { font-size: 22px; }
.hint { font-size: 12px; color: #999; }
.toolbar { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 16px; }
.filter-input, .filter-select { padding: 6px 10px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 13px; font-family: inherit; }
.filter-input { width: 200px; }
.btn-query, .btn-reset { padding: 6px 16px; border-radius: 4px; font-size: 13px; cursor: pointer; }
.btn-query { background: var(--yq-red); color: #fff; border: none; }
.btn-reset { background: #fff; border: 1px solid var(--yq-gray-200); }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; overflow: hidden; }
.data-table th { text-align: left; padding: 10px 12px; border-bottom: 2px solid var(--yq-gray-200); font-size: 12px; color: #888; }
.data-table td { padding: 10px 12px; border-bottom: 1px solid var(--yq-gray-100); }
.nowrap { white-space: nowrap; }
.detail-cell { max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.badge { padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: 600; }
.badge.ok { background: #d4edda; color: #155724; }
.badge.fail { background: #f8d7da; color: #721c24; }
.empty { text-align: center; color: #999; padding: 20px !important; }
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 12px; margin-top: 16px; font-size: 13px; color: #666; }
.pagination button { padding: 5px 14px; border: 1px solid var(--yq-gray-200); border-radius: 4px; background: #fff; font-size: 13px; cursor: pointer; }
.pagination button:disabled { opacity: 0.45; cursor: not-allowed; }
</style>
