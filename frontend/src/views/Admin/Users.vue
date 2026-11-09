<template>
  <div>
    <div class="header"><h1>用户管理</h1><button @click="openModal()" class="btn-add">+ 添加用户</button></div>
    <table class="data-table">
      <thead><tr><th>用户名</th><th>姓名</th><th>工号</th><th>工作邮箱</th><th>角色</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr v-for="u in users" :key="u.id">
          <td>{{ u.username }}</td>
          <td>{{ u.displayName || '—' }}</td>
          <td>{{ u.employeeNumber || '—' }}</td>
          <td class="email-cell">
            <a v-if="u.workEmail" :href="`mailto:${u.workEmail}`">{{ u.workEmail }}</a>
            <span v-else>—</span>
          </td>
          <td>{{ roleLabel(u.role) }}</td>
          <td><span class="badge" :class="u.active ? 'active' : 'inactive'">{{ u.active ? '启用' : '禁用' }}</span></td>
          <td>
            <button @click="openModal(u)" class="btn-edit">编辑</button>
            <button v-if="u.id !== currentUserId" @click="openPwdModal(u)" class="btn-pwd">重置密码</button>
            <button v-if="u.id !== currentUserId" @click="requestDelete(u)" class="btn-del">删除</button>
          </td>
        </tr>
        <tr v-if="!users.length"><td colspan="7" class="empty">暂无用户</td></tr>
      </tbody>
    </table>
    <div v-if="modalOpen" class="modal-overlay">
      <div class="modal">
        <h2>{{ editing?.id ? '编辑用户' : '添加用户' }}</h2>
        <div class="field"><label>用户名 *</label><input v-model="form.username" /></div>
        <div class="field"><label>姓名</label><input v-model="form.displayName" /></div>
        <div class="field"><label>工号</label><input v-model="form.employeeNumber" maxlength="50" /></div>
        <div class="field">
          <label>工作邮箱{{ form.role === 'SALES' ? ' *' : '' }}</label>
          <input v-model="form.workEmail" type="email" maxlength="150" placeholder="name@yqwiremesh.com" />
        </div>
        <div class="field"><label>角色</label>
          <select v-model="form.role">
            <option value="ADMIN">管理员</option>
            <option value="SALES">业务员</option>
          </select>
        </div>
        <div class="field"><label>状态</label>
          <select v-model="form.active">
            <option :value="true">启用</option>
            <option :value="false">停用</option>
          </select>
        </div>
        <div class="field"><label>密码{{ editing?.id ? '（留空不修改）' : ' *' }}</label><input v-model="form.password" type="password" /></div>
        <div v-if="saveError" class="save-error">{{ saveError }}</div>
        <div class="actions">
          <button @click="save" class="btn-save">{{ saving ? '保存中...' : '保存' }}</button>
          <button @click="modalOpen = false" class="btn-cancel">取消</button>
        </div>
      </div>
    </div>
    <div v-if="pwdModalOpen" class="modal-overlay">
      <div class="modal">
        <h2>重置密码</h2>
        <p class="pwd-info">{{ pwdTarget?.displayName || pwdTarget?.username }}</p>
        <div class="field"><label>新密码 *</label><input v-model="newPassword" type="password" placeholder="至少 6 位" /></div>
        <div class="field"><label>确认新密码 *</label><input v-model="confirmPassword" type="password" /></div>
        <div v-if="pwdError" class="error">{{ pwdError }}</div>
        <div v-if="pwdSuccess" class="success">密码已重置</div>
        <div class="actions">
          <button @click="confirmResetPassword" class="btn-save" :disabled="resetting">{{ resetting ? '处理中...' : '确定重置' }}</button>
          <button @click="closePwdModal" class="btn-cancel">取消</button>
        </div>
      </div>
    </div>
    <ConfirmDialog
      :visible="!!deleteTarget"
      title="删除用户"
      :message="`确定要删除用户「${deleteTarget?.displayName || deleteTarget?.username || ''}」吗？删除后不可恢复。`"
      :loading="deleting"
      @cancel="deleteTarget = null"
      @confirm="doDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { api } from '../../api'
import ConfirmDialog from '../../components/ConfirmDialog.vue'

const currentUserId = computed(() => JSON.parse(localStorage.getItem('yqmetals_user') || '{}').id)
const users = ref<any[]>([])
const modalOpen = ref(false)
const editing = ref<any>(null)
const saving = ref(false)
const saveError = ref('')
const deleteTarget = ref<any>(null)
const deleting = ref(false)
const pwdModalOpen = ref(false)
const pwdTarget = ref<any>(null)
const newPassword = ref('')
const confirmPassword = ref('')
const pwdError = ref('')
const pwdSuccess = ref(false)
const resetting = ref(false)
const form = ref({ username: '', displayName: '', employeeNumber: '', workEmail: '', role: 'SALES', password: '', active: true })

async function load() { users.value = await api.users.list() }

function openModal(u?: any) {
  editing.value = u
  form.value = u
    ? {
        username: u.username,
        displayName: u.displayName ?? '',
        employeeNumber: u.employeeNumber ?? '',
        workEmail: u.workEmail ?? '',
        role: u.role,
        password: '',
        active: u.active ?? true,
      }
    : { username: '', displayName: '', employeeNumber: '', workEmail: '', role: 'SALES', password: '', active: true }
  saveError.value = ''
  modalOpen.value = true
}

async function save() {
  saving.value = true
  saveError.value = ''
  try {
    const workEmail = form.value.workEmail.trim()
    if (form.value.role === 'SALES' && !workEmail) {
      saveError.value = '业务员必须填写工作邮箱'
      return
    }
    if (workEmail && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(workEmail)) {
      saveError.value = '请输入有效的工作邮箱'
      return
    }
    const payload = {
      username: form.value.username.trim(),
      displayName: form.value.displayName.trim(),
      employeeNumber: form.value.employeeNumber.trim(),
      workEmail,
      role: form.value.role,
      active: form.value.active,
    }
    if (editing.value?.id) {
      const data: any = { ...payload }
      if (form.value.password) data['password'] = form.value.password
      await api.users.update(editing.value.id, data)
    } else {
      await api.users.create({ ...payload, password: form.value.password })
    }
    modalOpen.value = false
    await load()
  } catch (err: any) {
    saveError.value = '保存失败: ' + err.message
  } finally {
    saving.value = false
  }
}

function requestDelete(u: any) {
  deleteTarget.value = u
}

function openPwdModal(u: any) {
  pwdTarget.value = u
  newPassword.value = ''
  confirmPassword.value = ''
  pwdError.value = ''
  pwdSuccess.value = false
  pwdModalOpen.value = true
}

function closePwdModal() {
  pwdModalOpen.value = false
  pwdTarget.value = null
  pwdSuccess.value = false
}

async function confirmResetPassword() {
  if (!newPassword.value || newPassword.value.length < 6) {
    pwdError.value = '密码至少 6 位'
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    pwdError.value = '两次输入的密码不一致'
    return
  }
  resetting.value = true
  pwdError.value = ''
  try {
    await api.users.resetPassword(pwdTarget.value.id, newPassword.value)
    pwdSuccess.value = true
    setTimeout(() => closePwdModal(), 900)
  } catch (e: any) {
    pwdError.value = '重置失败: ' + e.message
  } finally {
    resetting.value = false
  }
}

async function doDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await api.users.delete(deleteTarget.value.id)
    deleteTarget.value = null
    await load()
  } catch (err: any) {
    alert('删除失败: ' + err.message)
  } finally {
    deleting.value = false
  }
}

function roleLabel(r: string) {
  const map: Record<string, string> = { ADMIN: '管理员', SALES: '业务员' }
  return map[r] || r
}

onMounted(load)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
h1 { font-size: 22px; }
.btn-add { background: var(--yq-red); color: #fff; border: none; padding: 8px 16px; border-radius: 4px; font-size: 13px; cursor: pointer; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; overflow: hidden; }
.data-table th { text-align: left; padding: 10px 12px; border-bottom: 2px solid var(--yq-gray-200); font-size: 12px; color: #888; }
.data-table td { padding: 10px 12px; border-bottom: 1px solid var(--yq-gray-100); }
.email-cell { max-width: 220px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.email-cell a { color: var(--yq-red); text-decoration: none; }
.email-cell a:hover { text-decoration: underline; }
.btn-edit, .btn-del { padding: 4px 10px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 12px; cursor: pointer; background: #fff; margin-right: 4px; }
.btn-del { color: red; border-color: #fcc; }
.btn-pwd { padding: 4px 10px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 12px; cursor: pointer; background: #fff; margin-right: 4px; }
.btn-pwd:hover { border-color: var(--yq-red); color: var(--yq-red); }
.pwd-info { font-size: 13px; color: #666; margin-bottom: 14px; }
.error { color: red; font-size: 13px; margin-bottom: 12px; }
.success { color: #155724; font-size: 13px; margin-bottom: 12px; }
.badge { padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: 600; }
.badge.active { background: #d4edda; color: #155724; }
.badge.inactive { background: var(--yq-gray-200); color: #666; }
.empty { text-align: center; color: #999; padding: 20px !important; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; padding: 24px; border-radius: 8px; width: 420px; max-width: 90vw; }
.modal h2 { margin-bottom: 16px; }
.field { margin-bottom: 12px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 4px; }
.field input, .field select { width: 100%; padding: 8px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 14px; }
.save-error { color: red; font-size: 13px; margin-bottom: 12px; }
.actions { display: flex; gap: 8px; margin-top: 16px; }
.btn-save { background: var(--yq-red); color: #fff; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.btn-cancel { background: var(--yq-gray-200); border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
</style>
