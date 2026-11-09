<template>
  <div>
    <h1>询单管理</h1>
    <div class="toolbar">
      <select v-model="filterStatus" class="filter">
        <option value="">全部状态</option>
        <option value="NEW">待分配</option>
        <option value="ASSIGNED">已分配</option>
        <option value="IN_PROGRESS">处理中</option>
        <option value="CLOSED">已关闭</option>
      </select>
    </div>
    <table class="data-table">
      <thead><tr><th>询盘日期</th><th>客户姓名</th><th>客户所在公司</th><th>客户邮箱</th><th>客户所在国家/地区</th><th>客户感兴趣产品</th><th>询盘内容</th><th>状态</th><th v-if="isAdmin">分配给</th></tr></thead>
      <tbody>
        <tr v-for="q in filtered" :key="q.id">
          <td>{{ formatDate(q.createTime) }}</td>
          <td>{{ q.name }}</td>
          <td>{{ q.company || '—' }}</td>
          <td><a :href="`mailto:${q.email}`">{{ q.email }}</a></td>
          <td>{{ q.country || '—' }}</td>
          <td>{{ q.interestedProduct || q.categoryId || '—' }}</td>
          <td class="message-cell" :title="q.message" @click="openDetail(q)">{{ q.message || '—' }}</td>
          <td><span class="badge" :class="q.status">{{ statusLabel(q.status) }}</span></td>
          <td v-if="isAdmin">
            <span v-if="q.assignedTo" class="assignee">{{ userName(q.assignedTo) }}</span>
            <button class="btn-assign" @click="openAssign(q)">
              {{ q.assignedTo ? '重新分配' : '分配' }}
            </button>
          </td>
        </tr>
        <tr v-if="!filtered.length"><td :colspan="isAdmin ? 9 : 8" class="empty">暂无询单</td></tr>
      </tbody>
    </table>

    <div v-if="detailOpen" class="modal-overlay">
      <div class="modal detail-modal">
        <h2>询盘详情</h2>
        <div class="detail-grid">
          <div><label>询盘日期</label><span>{{ formatDate(detailTarget?.createTime) }}</span></div>
          <div><label>客户姓名</label><span>{{ detailTarget?.name }}</span></div>
          <div><label>客户所在公司</label><span>{{ detailTarget?.company || '—' }}</span></div>
          <div><label>客户邮箱</label><span><a :href="`mailto:${detailTarget?.email}`">{{ detailTarget?.email }}</a></span></div>
          <div><label>客户所在国家/地区</label><span>{{ detailTarget?.country || '—' }}</span></div>
          <div><label>客户感兴趣产品</label><span>{{ detailTarget?.interestedProduct || detailTarget?.categoryId || '—' }}</span></div>
          <div><label>状态</label><span>{{ statusLabel(detailTarget?.status) }}</span></div>
          <div v-if="isAdmin"><label>负责人</label><span>{{ detailTarget?.assignedTo ? userName(detailTarget.assignedTo) : '未分配' }}</span></div>
        </div>
        <div class="detail-message">
          <label>询盘内容</label>
          <div class="message-body">{{ detailTarget?.message || '—' }}</div>
        </div>
        <div class="actions">
          <button @click="detailOpen = false" class="btn-cancel">关闭</button>
        </div>
      </div>
    </div>

    <div v-if="assignModalOpen" class="modal-overlay">
      <div class="modal">
        <h2>{{ assignTarget?.assignedTo ? '重新分配询盘' : '分配询盘' }}</h2>
        <p class="assign-info">{{ assignTarget?.name }} · {{ assignTarget?.company || '—' }} · {{ assignTarget?.country || '—' }}</p>
        <div class="field">
          <label>选择业务员</label>
          <select v-model="selectedAssignee">
            <option :value="null">请选择...</option>
            <option v-for="u in salesUsers" :key="u.id" :value="u.id">{{ u.displayName || u.username }}</option>
          </select>
        </div>
        <div v-if="assignError" class="assign-error">{{ assignError }}</div>
        <div class="actions">
          <button @click="confirmAssign" class="btn-save" :disabled="assigning">{{ assigning ? '分配中...' : '确定' }}</button>
          <button @click="closeAssignModal" class="btn-cancel">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { api } from '../../api'

const isAdmin = computed(() => localStorage.getItem('yqmetals_role') === 'ADMIN')
const inquiries = ref<any[]>([])
const users = ref<any[]>([])
const filterStatus = ref('')
const assignModalOpen = ref(false)
const assignTarget = ref<any>(null)
const selectedAssignee = ref<number | null>(null)
const assigning = ref(false)
const assignError = ref('')
const detailOpen = ref(false)
const detailTarget = ref<any>(null)

const salesUsers = computed(() => users.value.filter((u: any) => u.role === 'SALES'))

const filtered = computed(() =>
  filterStatus.value ? inquiries.value.filter((q: any) => q.status === filterStatus.value) : inquiries.value
)

async function load() {
  inquiries.value = isAdmin.value ? await api.inquiries.getAll() : await api.inquiries.getMine()
  if (isAdmin.value) users.value = await api.users.list()
}

function userName(id: number) {
  return users.value.find((u: any) => u.id === id)?.displayName || `#${id}`
}

function openAssign(q: any) {
  assignTarget.value = q
  selectedAssignee.value = q.assignedTo ?? null
  assignError.value = ''
  assignModalOpen.value = true
}

function openDetail(q: any) {
  detailTarget.value = q
  detailOpen.value = true
}

function closeAssignModal() {
  assignModalOpen.value = false
  assignTarget.value = null
  selectedAssignee.value = null
}

async function confirmAssign() {
  if (!selectedAssignee.value) {
    assignError.value = '请选择业务员'
    return
  }
  assigning.value = true
  assignError.value = ''
  try {
    const result = await api.inquiries.assign(assignTarget.value.id, selectedAssignee.value)
    closeAssignModal()
    await load()
    if (result && result.emailSent === false) {
      alert('分配成功，但通知邮件未发送：' + (result.emailWarning || '请检查邮件通知配置'))
    }
  } catch (e: any) {
    assignError.value = '分配失败: ' + e.message
  } finally {
    assigning.value = false
  }
}

function formatDate(d: string) { return new Date(d).toLocaleDateString('zh-CN') }
function statusLabel(s: string) {
  const map: Record<string, string> = { NEW: '待分配', ASSIGNED: '已分配', IN_PROGRESS: '处理中', CLOSED: '已关闭' }
  return map[s] || s
}
onMounted(load)
</script>

<style scoped>
h1 { font-size: 22px; margin-bottom: 16px; }
.toolbar { margin-bottom: 16px; }
.filter { padding: 6px 12px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 13px; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; overflow: hidden; }
.data-table th { text-align: left; padding: 10px 12px; border-bottom: 2px solid var(--yq-gray-200); font-size: 12px; color: #888; }
.data-table td { padding: 10px 12px; border-bottom: 1px solid var(--yq-gray-100); }
.data-table a { color: var(--yq-red); text-decoration: none; }
.message-cell { max-width: 220px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; cursor: pointer; }
.message-cell:hover { color: var(--yq-red); }
.badge { padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: 600; }
.badge.NEW { background: #fef3cd; color: #856404; }
.badge.ASSIGNED { background: #d1ecf1; color: #0c5460; }
.badge.IN_PROGRESS { background: #d4edda; color: #155724; }
.badge.CLOSED { background: var(--yq-gray-200); color: #666; }
.assignee { font-size: 12px; color: #666; margin-right: 8px; }
.btn-assign { padding: 4px 10px; border: 1px solid var(--yq-red); color: var(--yq-red); background: #fff; border-radius: 4px; font-size: 12px; cursor: pointer; }
.btn-assign:hover { background: var(--yq-red); color: #fff; }
.empty { text-align: center; color: #999; padding: 20px !important; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; padding: 24px; border-radius: 8px; width: 420px; max-width: 90vw; }
.modal h2 { margin-bottom: 8px; font-size: 18px; }
.assign-info { font-size: 13px; color: #666; margin-bottom: 16px; }
.field { margin-bottom: 12px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 4px; }
.field select { width: 100%; padding: 8px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 14px; }
.assign-error { color: red; font-size: 13px; margin-bottom: 12px; }
.actions { display: flex; gap: 8px; margin-top: 16px; }
.btn-save { background: var(--yq-red); color: #fff; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-cancel { background: var(--yq-gray-200); border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.detail-modal { width: 560px; }
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px 24px; margin-bottom: 16px; }
.detail-grid > div { display: flex; flex-direction: column; gap: 2px; }
.detail-grid label { font-size: 12px; color: #999; }
.detail-grid span { font-size: 14px; color: #333; word-break: break-word; }
.detail-message label { display: block; font-size: 12px; color: #999; margin-bottom: 6px; }
.message-body { white-space: pre-wrap; word-break: break-word; font-size: 14px; line-height: 1.7; color: #333; background: var(--yq-gray-50); border: 1px solid var(--yq-gray-100); border-radius: 4px; padding: 12px; max-height: 240px; overflow-y: auto; }
</style>
