<template>
  <div>
    <div class="header"><h1>Users</h1><button @click="openModal()" class="btn-add">+ Add User</button></div>
    <table class="data-table">
      <thead><tr><th>Username</th><th>Display Name</th><th>Role</th><th>Actions</th></tr></thead>
      <tbody>
        <tr v-for="u in users" :key="u.id">
          <td>{{ u.username }}</td>
          <td>{{ u.displayName }}</td>
          <td><span class="badge" :class="u.role">{{ u.role }}</span></td>
          <td><button @click="openModal(u)" class="btn-edit">Edit</button></td>
        </tr>
        <tr v-if="!users.length"><td colspan="4" class="empty">No users</td></tr>
      </tbody>
    </table>
    <div v-if="modalOpen" class="modal-overlay" @click.self="modalOpen = false">
      <div class="modal">
        <h2>{{ editing?.id ? 'Edit' : 'Add' }} User</h2>
        <div class="field" v-if="!editing?.id"><label>Username *</label><input v-model="form.username" /></div>
        <div class="field" v-if="!editing?.id"><label>Password *</label><input v-model="form.password" type="password" /></div>
        <div class="field"><label>Display Name *</label><input v-model="form.displayName" /></div>
        <div class="field"><label>Role</label>
          <select v-model="form.role">
            <option>ADMIN</option><option>SALES</option>
          </select>
        </div>
        <div class="actions">
          <button @click="save" class="btn-save">Save</button>
          <button @click="modalOpen = false" class="btn-cancel">Cancel</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../api'

const users = ref<any[]>([])
const modalOpen = ref(false)
const editing = ref<any>(null)
const form = ref({ username: '', password: '', displayName: '', role: 'SALES' })

async function load() { users.value = await api.users.list() }

function openModal(u?: any) {
  editing.value = u
  form.value = u ? { username: u.username, password: '', displayName: u.displayName, role: u.role }
    : { username: '', password: '', displayName: '', role: 'SALES' }
  modalOpen.value = true
}

async function save() {
  if (editing.value?.id) {
    await api.users.update(editing.value.id, { displayName: form.value.displayName, role: form.value.role })
  } else {
    await api.users.create(form.value)
  }
  modalOpen.value = false
  await load()
}

onMounted(load)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
h1 { font-size: 22px; }
.btn-add { background: var(--yq-red); color: #fff; border: none; padding: 8px 16px; border-radius: 4px; font-size: 13px; cursor: pointer; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; overflow: hidden; }
.data-table th { text-align: left; padding: 10px 12px; border-bottom: 2px solid var(--yq-gray-200); font-size: 12px; color: #888; text-transform: uppercase; }
.data-table td { padding: 10px 12px; border-bottom: 1px solid var(--yq-gray-100); }
.badge { padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: 600; }
.badge.ADMIN { background: #fce4ec; color: #c62828; }
.badge.SALES { background: #e3f2fd; color: #1565c0; }
.btn-edit { padding: 4px 10px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 12px; cursor: pointer; background: #fff; }
.empty { text-align: center; color: #999; padding: 20px !important; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; padding: 24px; border-radius: 8px; width: 400px; max-width: 90vw; }
.modal h2 { margin-bottom: 16px; }
.field { margin-bottom: 12px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 4px; }
.field input, .field select { width: 100%; padding: 8px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 14px; }
.actions { display: flex; gap: 8px; margin-top: 16px; }
.btn-save { background: var(--yq-red); color: #fff; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.btn-cancel { background: var(--yq-gray-200); border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
</style>