<template>
  <div>
    <div class="header"><h1>邮件通知</h1></div>

    <div class="panel">
      <h2>发件设置</h2>
      <p class="hint">配置后，前台客户提交的询盘会自动通知到下方指定的收件邮箱。密码留空时不会真正发送邮件。</p>
      <div class="field"><label>SMTP 主机</label><input v-model="form['mail.smtp.host']" placeholder="smtp.gmail.com" /></div>
      <div class="field"><label>SMTP 端口</label><input v-model="form['mail.smtp.port']" placeholder="587" /></div>
      <div class="field"><label>发件邮箱</label><input v-model="form['mail.smtp.username']" placeholder="sales@yqwiremesh.com" /></div>
      <div class="field"><label>邮箱密码 / 授权码</label><input v-model="form['mail.smtp.password']" type="password" placeholder="留空则不发送邮件" /></div>
      <div class="field"><label>发件人（可选）</label><input v-model="form['mail.from']" placeholder="留空时与发件邮箱相同" /></div>
      <div class="field toggle"><label><input type="checkbox" v-model="form['mail.smtp.ssl']" /> 使用 SSL（SMTPS，通常端口 465）</label></div>
      <div class="field toggle"><label><input type="checkbox" v-model="form['mail.smtp.starttls']" /> 使用 STARTTLS（通常端口 587）</label></div>
    </div>

    <div class="panel">
      <h2>收件设置</h2>
      <p class="hint">新询盘通知发送到的邮箱。当前功能只负责"发送通知"，不需要从邮箱收信，因此不配置收信服务器。</p>
      <div class="field"><label>收件邮箱</label><input v-model="form['mail.recipient']" placeholder="sales@yqwiremesh.com" /></div>
    </div>

    <div v-if="saveError" class="save-error">{{ saveError }}</div>
    <div v-if="saved" class="save-success">已保存</div>
    <div class="actions">
      <button @click="save" class="btn-save" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../api'

const form = ref<Record<string, any>>({
  'mail.smtp.host': '',
  'mail.smtp.port': '',
  'mail.smtp.username': '',
  'mail.smtp.password': '',
  'mail.from': '',
  'mail.smtp.ssl': false,
  'mail.smtp.starttls': true,
  'mail.recipient': '',
})
const saving = ref(false)
const saved = ref(false)
const saveError = ref('')
const loaded = ref(false)

/** 数据库里存的是字符串，checkbox 需要布尔值。 */
function normalizeBooleans() {
  ;['mail.smtp.ssl', 'mail.smtp.starttls'].forEach(key => {
    form.value[key] = String(form.value[key]).toLowerCase() === 'true'
  })
}

async function load() {
  try {
    const settings = await api.settings.get()
    form.value = { ...form.value, ...settings }
    normalizeBooleans()
    loaded.value = true
  } catch (e: any) {
    saveError.value = '加载失败，请刷新页面后重试: ' + e.message
    loaded.value = false
  }
}

async function save() {
  if (!loaded.value) {
    saveError.value = '设置尚未加载成功，为避免覆盖已有配置，请刷新页面后重试'
    return
  }
  saving.value = true
  saved.value = false
  saveError.value = ''
  try {
    await api.settings.save(form.value)
    saved.value = true
  } catch (e: any) {
    saveError.value = '保存失败: ' + e.message
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.header { margin-bottom: 20px; }
h1 { font-size: 22px; }
.panel { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 24px; max-width: 680px; margin-bottom: 20px; }
.panel h2 { font-size: 17px; margin-bottom: 6px; }
.hint { font-size: 13px; color: #888; margin-bottom: 20px; }
.field { margin-bottom: 14px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 4px; }
.field input { width: 100%; padding: 8px 10px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 14px; font-family: inherit; }
.field input:focus { outline: none; border-color: var(--yq-red); }
.field.toggle label { display: inline-flex; align-items: center; font-weight: 400; }
.field.toggle input { width: auto; margin-right: 6px; }
.save-error { color: red; font-size: 13px; margin: 8px 0; }
.save-success { color: #155724; font-size: 13px; margin: 8px 0; }
.actions { margin-bottom: 32px; }
.btn-save { background: var(--yq-red); color: #fff; border: none; padding: 9px 24px; border-radius: 4px; font-size: 13px; cursor: pointer; }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
