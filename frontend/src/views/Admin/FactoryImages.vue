<template>
  <div>
    <div class="header">
      <h1>工厂图片</h1>
      <button @click="openModal()" class="btn-add">+ 添加图片</button>
    </div>
    <p class="hint">此处图片会展示在前台“工厂实景”页面，按排序从小到大显示。</p>
    <div class="list">
      <div v-for="img in images" :key="img.id" class="row">
        <img v-if="img.imageUrl" :src="img.imageUrl" class="thumb" />
        <span v-else class="thumb empty">未上传</span>
        <span class="caption">{{ img.caption || '（无说明）' }}</span>
        <span class="meta">排序: {{ img.sort }}</span>
        <button @click="openModal(img)" class="btn-edit">编辑</button>
        <button @click="requestDelete(img)" class="btn-del">删除</button>
      </div>
      <div v-if="!images.length" class="empty-row">暂无图片</div>
    </div>

    <div v-if="modalOpen" class="modal-overlay">
      <div class="modal">
        <h2>{{ editing?.id ? '编辑工厂图片' : '添加工厂图片' }}</h2>
        <div class="field">
          <label>图片</label>
          <input type="file" accept="image/*" @change="onFileSelect" />
          <img v-if="form.imageUrl" :src="form.imageUrl" class="preview" />
        </div>
        <div class="field"><label>说明</label><input v-model="form.caption" placeholder="如：Production Workshop" /></div>
        <div class="field"><label>排序</label><input v-model.number="form.sort" type="number" /></div>
        <div v-if="saveError" class="save-error">{{ saveError }}</div>
        <div v-if="saveSuccess" class="save-success">已保存</div>
        <div class="actions">
          <button @click="save" class="btn-save" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
          <button @click="closeModal" class="btn-cancel">取消</button>
        </div>
      </div>
    </div>

    <ConfirmDialog
      :visible="!!deleteTarget"
      title="删除工厂图片"
      :message="`确定要删除图片「${deleteTarget?.caption || '未命名'}」吗？删除后不可恢复。`"
      :loading="deleting"
      @cancel="deleteTarget = null"
      @confirm="doDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../api'
import ConfirmDialog from '../../components/ConfirmDialog.vue'

const images = ref<any[]>([])
const modalOpen = ref(false)
const editing = ref<any>(null)
const saving = ref(false)
const saveError = ref('')
const saveSuccess = ref(false)
const deleteTarget = ref<any>(null)
const deleting = ref(false)
const form = ref({ imageUrl: '', caption: '', sort: 0 })

async function load() {
  try {
    images.value = await api.factoryImages.adminList()
  } catch (e: any) {
    images.value = []
    saveError.value = '加载失败: ' + e.message
  }
}

function openModal(img?: any) {
  editing.value = img
  form.value = img
    ? { imageUrl: img.imageUrl ?? '', caption: img.caption ?? '', sort: img.sort ?? 0 }
    : { imageUrl: '', caption: '', sort: images.value.length + 1 }
  saveError.value = ''
  saveSuccess.value = false
  modalOpen.value = true
}

function closeModal() {
  modalOpen.value = false
  saveSuccess.value = false
}

async function onFileSelect(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  try {
    const res = await api.upload.image(file)
    form.value.imageUrl = res.url
  } catch (err: any) {
    alert('上传失败: ' + err.message)
  }
}

async function save() {
  saving.value = true
  saveError.value = ''
  saveSuccess.value = false
  try {
    if (!form.value.imageUrl) {
      saveError.value = '请先上传图片'
      saving.value = false
      return
    }
    if (editing.value?.id) await api.factoryImages.update(editing.value.id, form.value)
    else await api.factoryImages.create(form.value)
    saveSuccess.value = true
    await load()
    setTimeout(() => closeModal(), 900)
  } catch (err: any) {
    saveError.value = '保存失败: ' + err.message
  } finally {
    saving.value = false
  }
}

function requestDelete(img: any) {
  deleteTarget.value = img
}

async function doDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await api.factoryImages.delete(deleteTarget.value.id)
    deleteTarget.value = null
    await load()
  } catch (err: any) {
    alert('删除失败: ' + err.message)
  } finally {
    deleting.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
h1 { font-size: 22px; }
.hint { font-size: 13px; color: #888; margin-bottom: 16px; }
.btn-add { background: var(--yq-red); color: #fff; border: none; padding: 8px 16px; border-radius: 4px; font-size: 13px; cursor: pointer; }
.list { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 8px; }
.row { display: flex; align-items: center; gap: 12px; padding: 10px 12px; border-radius: 4px; font-size: 14px; }
.row:hover { background: var(--yq-gray-50); }
.thumb { width: 64px; height: 44px; border-radius: 4px; object-fit: cover; flex-shrink: 0; border: 1px solid var(--yq-gray-200); }
.thumb.empty { display: flex; align-items: center; justify-content: center; background: var(--yq-gray-100); color: #999; font-size: 11px; }
.caption { flex: 1; font-weight: 600; }
.meta { font-size: 12px; color: #999; }
.btn-edit, .btn-del { padding: 4px 10px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 12px; cursor: pointer; background: #fff; }
.btn-del { color: red; border-color: #fcc; }
.empty-row { text-align: center; color: #999; padding: 24px; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; padding: 24px; border-radius: 8px; width: 480px; max-width: 90vw; max-height: 90vh; overflow-y: auto; }
.modal h2 { margin-bottom: 16px; }
.field { margin-bottom: 12px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 4px; }
.field input { width: 100%; padding: 8px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 14px; }
.preview { width: 100%; max-height: 220px; object-fit: cover; border-radius: 4px; margin-top: 8px; border: 1px solid #ddd; }
.save-error { color: red; font-size: 13px; margin-bottom: 12px; }
.save-success { color: #155724; font-size: 13px; margin-bottom: 12px; }
.actions { display: flex; gap: 8px; margin-top: 16px; }
.btn-save { background: var(--yq-red); color: #fff; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-cancel { background: var(--yq-gray-200); border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
</style>
