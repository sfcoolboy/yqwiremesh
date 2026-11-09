<template>
  <div>
    <div class="header"><h1>产品管理</h1><button @click="openModal()" class="btn-add">+ 添加产品</button></div>
    <table class="data-table">
      <thead><tr><th>产品名称</th><th>分类</th><th>排序</th><th>图片</th><th>操作</th></tr></thead>
      <tbody>
        <tr v-for="p in products" :key="p.id">
          <td>{{ p.name }}</td>
          <td>{{ categoryName(p.categoryId) }}</td>
          <td>{{ p.sort }}</td>
          <td><img v-if="p.imageUrl" :src="p.imageUrl" class="thumb" /></td>
          <td>
            <button @click="openModal(p)" class="btn-edit">编辑</button>
            <button @click="requestDelete(p)" class="btn-del">删除</button>
          </td>
        </tr>
        <tr v-if="!products.length"><td colspan="5" class="empty">暂无产品</td></tr>
      </tbody>
    </table>
    <div v-if="modalOpen" class="modal-overlay">
      <div class="modal">
        <h2>{{ editing?.id ? '编辑产品' : '添加产品' }}</h2>
        <div class="field"><label>产品名称 *</label><input v-model="form.name" /></div>
        <div class="field"><label>所属分类 *</label>
          <select v-model.number="form.categoryId" @change="onCategoryChange">
            <option :value="null">请选择...</option>
            <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.nameEn }}</option>
          </select>
        </div>
        <div class="field"><label>排序</label><input v-model.number="form.sort" type="number" /></div>
        <div v-if="catParams.length" class="field">
          <label>产品参数</label>
          <div v-for="p in catParams" :key="p.id" class="param-input-row">
            <span class="param-label">{{ p.name }}</span>
            <input v-model="form.specs[p.id]" :placeholder="'请输入' + p.name" />
            <span v-if="p.unit" class="param-unit">{{ p.unit }}</span>
          </div>
        </div>
        
        <div class="field">
          <label>产品图片</label>
          <input
            type="file"
            accept="image/*"
            :disabled="uploadingImage"
            @change="onFileSelect($event, 'imageUrl')"
          />
          <div v-if="uploadingImage" class="upload-progress">图片上传中...</div>
          <img v-if="form.imageUrl" :src="form.imageUrl" class="preview" />
        </div>
        <div v-if="saveError" class="save-error">{{ saveError }}</div>
        <div class="actions">
          <button
            @click="save"
            class="btn-save"
            :disabled="saving || uploadingImage"
          >
            {{ uploadingImage ? '图片上传中...' : saving ? '保存中...' : '保存' }}
          </button>
          <button @click="modalOpen = false" class="btn-cancel">取消</button>
        </div>
      </div>
    </div>
    <ConfirmDialog
      :visible="!!deleteTarget"
      title="删除产品"
      :message="`确定要删除产品「${deleteTarget?.name || ''}」吗？删除后不可恢复。`"
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

const products = ref<any[]>([])
const categories = ref<any[]>([])
const modalOpen = ref(false)
const editing = ref<any>(null)
const saving = ref(false)
const uploadingImage = ref(false)
const saveError = ref('')
const deleteTarget = ref<any>(null)
const deleting = ref(false)
const form = ref({
  name: '',
  categoryId: null as number | null,
  sort: 0,
  imageUrl: '',
  specs: {} as Record<string, string>,
})

const catMap = ref(new Map<number, string>())
const catParams = ref<any[]>([])
const categoryName = (id?: number) => id ? (catMap.value.get(id) ?? String(id)) : '—'

async function load() {
  products.value = await api.products.list()
  const cats = await api.categories.tree()
  categories.value = cats
  const m = new Map<number, string>()
  cats.forEach((c: any) => { m.set(c.id, c.nameEn); (c.children || []).forEach((sc: any) => m.set(sc.id, sc.nameEn)) })
  catMap.value = m
}

function openModal(p?: any) {
  editing.value = p
  form.value = p ? {
    name: p.name,
    categoryId: p.categoryId,
    sort: p.sort ?? 0,
    imageUrl: p.imageUrl ?? '',
    specs: normalizeSpecs(p.specs),
  }
    : { name: '', categoryId: null, sort: 0, imageUrl: '', specs: {} }
  saveError.value = ''
  modalOpen.value = true
  buildCatParams(form.value.categoryId)
}

function normalizeSpecs(raw: any): Record<string, string> {
  let parsed = raw
  if (typeof raw === 'string') {
    try {
      parsed = JSON.parse(raw)
    } catch {
      return {}
    }
  }
  if (Array.isArray(parsed)) {
    return Object.fromEntries(
      parsed
        .filter((item: any) => item && item.id !== undefined)
        .map((item: any) => [String(item.id), String(item.value ?? '')]),
    )
  }
  if (parsed && typeof parsed === 'object') {
    return Object.fromEntries(
      Object.entries(parsed).map(([key, value]) => [key, String(value ?? '')]),
    )
  }
  return {}
}

async function onFileSelect(e: Event, field: string) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  uploadingImage.value = true
  saveError.value = ''
  try {
    const res = await api.upload.image(file)
    form.value[field] = res.url
  } catch (err: any) {
    alert('上传失败: ' + err.message)
  } finally {
    uploadingImage.value = false
    input.value = ''
  }
}


function buildCatParams(catId?: number) {
  catParams.value = []
  if (!catId) return
  const cat = categories.value.find((c: any) => c.id === catId)
  if (cat?.specs) {
    try { catParams.value = JSON.parse(cat.specs) } catch {}
  }
}

async function onCategoryChange() {
  buildCatParams(form.value.categoryId)
}

async function save() {
  if (uploadingImage.value) {
    saveError.value = '图片正在上传，请稍候再保存'
    return
  }
  saving.value = true
  saveError.value = ''
  try {
    const savePayload = { ...form.value, specs: JSON.stringify(buildSpecsJson()) }
    if (editing.value?.id) await api.products.update(editing.value.id, savePayload)
    else await api.products.create(savePayload)
    modalOpen.value = false
    await load()
  } catch (err: any) {
    saveError.value = '保存失败: ' + err.message
  } finally {
    saving.value = false
  }
}

function requestDelete(p: any) {
  deleteTarget.value = p
}

async function doDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await api.products.delete(deleteTarget.value.id)
    deleteTarget.value = null
    await load()
  } catch (err: any) {
    alert('删除失败: ' + err.message)
  } finally {
    deleting.value = false
  }
}


function buildSpecsJson() {
  const result: Record<string, string> = {}
  for (const p of catParams.value) {
    result[String(p.id)] = String(form.value.specs[String(p.id)] || '')
  }
  return result
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
.thumb { width: 50px; height: 50px; object-fit: cover; border-radius: 4px; }
.btn-edit, .btn-del { padding: 4px 10px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 12px; cursor: pointer; background: #fff; margin-right: 4px; }
.btn-del { color: red; border-color: #fcc; }
.empty { text-align: center; color: #999; padding: 20px !important; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; padding: 24px; border-radius: 8px; width: 480px; max-width: 90vw; }
.modal h2 { margin-bottom: 16px; }
.field { margin-bottom: 12px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 4px; }
.field input, .field select, .field textarea { width: 100%; padding: 8px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 14px; }
.preview { width: 100px; height: 100px; object-fit: cover; border-radius: 4px; margin-top: 8px; border: 1px solid #ddd; }
.upload-progress { margin-top: 6px; color: #1677ff; font-size: 12px; }
.save-error { color: red; font-size: 13px; margin-bottom: 12px; }
.actions { display: flex; gap: 8px; margin-top: 16px; }
.btn-save { background: var(--yq-red); color: #fff; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-cancel { background: var(--yq-gray-200); border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }

.param-input-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.param-label { width: 120px; font-size: 13px; font-weight: 600; color: #555; text-align: right; flex-shrink: 0; }
.param-input-row input { flex: 1; padding: 6px 8px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 13px; }
.param-unit { font-size: 12px; color: #999; width: 40px; }
</style>
