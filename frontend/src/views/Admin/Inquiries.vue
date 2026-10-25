<template>
  <div>
    <h1>Inquiries</h1>
    <div class="toolbar">
      <select v-model="filterStatus" class="filter">
        <option value="">All Status</option>
        <option value="NEW">New</option>
        <option value="ASSIGNED">Assigned</option>
        <option value="IN_PROGRESS">In Progress</option>
        <option value="CLOSED">Closed</option>
      </select>
    </div>
    <table class="data-table">
      <thead><tr><th>Date</th><th>Name</th><th>Company</th><th>Email</th><th>Country</th><th>Product</th><th>Status</th><th>Assigned To</th><th v-if="isAdmin">Actions</th></tr></thead>
      <tbody>
        <tr v-for="q in filtered" :key="q.id">
          <td>{{ formatDate(q.createTime) }}</td>
          <td>{{ q.name }}</td>
          <td>{{ q.company || '—' }}</td>
          <td><a :href="`mailto:${q.email}`">{{ q.email }}</a></td>
          <td>{{ q.country || '—' }}</td>
          <td>{{ q.categoryId || '—' }}</td>
          <td><span class="badge" :class="q.status">{{ q.status }}</span></td>
          <td>{{ q.assignedTo || '—' }}</td>
          <td v-if="isAdmin">
            <select v-model="q.assignedTo" @change="assign(q)" class="assign-select">
              <option :value="null">Unassigned</option>
              <option v-for="u in users" :key="u.id" :value="u.id">{{ u.displayName }}</option>
            </select>
          </td>
        </tr>
        <tr v-if="!filtered.length"><td colspan="9" class="empty">No inquiries found</td></tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { api } from '../../api'

const isAdmin = computed(() => localStorage.getItem('yqmetals_role') === 'ADMIN')
const inquiries = ref<any[]>([])
const users = ref<any[]>([])
const filterStatus = ref('')

const filtered = computed(() =>
  filterStatus.value ? inquiries.value.filter((q: any) => q.status === filterStatus.value) : inquiries.value
)

async function load() {
  inquiries.value = isAdmin.value ? await api.inquiries.getAll() : await api.inquiries.getMine()
  if (isAdmin.value) users.value = await api.users.list()
}

async function assign(q: any) {
  if (!q.assignedTo) return
  await api.inquiries.assign(q.id, q.assignedTo)
  await load()
}

function formatDate(d: string) { return new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' }) }

onMounted(load)
</script>

<style scoped>
h1 { font-size: 22px; margin-bottom: 16px; }
.toolbar { margin-bottom: 16px; }
.filter { padding: 6px 12px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 13px; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; overflow: hidden; }
.data-table th { text-align: left; padding: 10px 12px; border-bottom: 2px solid var(--yq-gray-200); font-size: 12px; color: #888; text-transform: uppercase; }
.data-table td { padding: 10px 12px; border-bottom: 1px solid var(--yq-gray-100); }
.data-table a { color: var(--yq-red); text-decoration: none; }
.badge { padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: 600; }
.badge.NEW { background: #fef3cd; color: #856404; }
.badge.ASSIGNED { background: #d1ecf1; color: #0c5460; }
.badge.IN_PROGRESS { background: #d4edda; color: #155724; }
.badge.CLOSED { background: var(--yq-gray-200); color: #666; }
.assign-select { padding: 4px 8px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 12px; }
.empty { text-align: center; color: #999; padding: 20px !important; }
</style>
