<template>
  <div>
    <div class="header">
      <h1>类目管理</h1>
      <button @click="openParamManager" class="btn-param">产品参数设置</button>
      <button @click="openTypeSelect" class="btn-add">+ 添加类目</button>
    </div>

    <div class="tabs">
      <button :class="{ active: type === 'PRODUCT' }" @click="switchType('PRODUCT')">产品类目</button>
      <button :class="{ active: type === 'NEWS' }" @click="switchType('NEWS')">新闻类目</button>
    </div>

    <div class="list">
      <div v-for="cat in categories" :key="cat.id" class="row">
        <img v-if="cat.imageUrl" :src="cat.imageUrl" class="thumb" />
        <span v-else class="thumb empty" />
        <span class="name">{{ cat.nameEn }}</span>
        <span class="meta">排序: {{ cat.sort }}</span>
        <button @click="openModal(cat)" class="btn-edit">编辑</button>
        <button @click="requestDelete(cat)" class="btn-del">删除</button>
      </div>
      <div v-if="!categories.length" class="empty">暂无类目</div>
    </div>

    <div v-if="modalOpen" class="modal-overlay">
      <div class="modal">
        <h2>{{ editing?.id ? '编辑类目' : '添加类目' }}（{{ type === 'PRODUCT' ? '产品' : '新闻' }}）</h2>
        <div class="field"><label>名称（英文）*</label><input v-model="form.nameEn" /></div>
        <div class="field"><label>排序</label><input v-model.number="form.sort" type="number" /></div>
        <div class="field"><label>描述</label><textarea v-model="form.description" rows="3" /></div>
        <div v-if="type === 'PRODUCT' && editing?.id" class="field">
          <label>参数展示顺序</label>
          <div v-if="categoryParams.length" class="param-order-list">
            <div
              v-for="(param, index) in categoryParams"
              :key="param.id"
              class="param-order-row"
              :class="{
                dragging: draggedParamIndex === index,
                'drag-over': dragOverParamIndex === index,
              }"
              draggable="true"
              @dragstart="startParamDrag(index, $event)"
              @dragover.prevent="setParamDragOver(index)"
              @drop.prevent="dropParam(index)"
              @dragend="endParamDrag"
            >
              <span class="drag-handle" title="拖动排序">
                <Rank class="drag-icon" />
              </span>
              <span class="param-order-index">{{ index + 1 }}</span>
              <span class="param-order-name">{{ param.name }}</span>
              <span class="param-order-unit">{{ param.unit || '无单位' }}</span>
            </div>
          </div>
          <div v-else class="param-order-empty">暂无已分配参数</div>
        </div>
        <div class="field">
          <label>分类图片</label>
          <input type="file" accept="image/*" @change="onFileSelect($event, 'imageUrl')" />
          <img v-if="form.imageUrl" :src="form.imageUrl" class="preview" />
        </div>
        <div v-if="saveError" class="save-error">{{ saveError }}</div>
        <div v-if="saveSuccess" class="save-success">已保存</div>
        <div class="actions">
          <button @click="save" class="btn-save">{{ saving ? '保存中...' : '保存' }}</button>
          <button @click="closeModal" class="btn-cancel">取消</button>
        </div>
      </div>
    </div>

    <div v-if="typeSelectOpen" class="modal-overlay" @click.self="typeSelectOpen = false">
      <div class="modal type-select-modal">
        <h2>选择类目类型</h2>
        <p class="type-hint">请先选择要添加的类目类型：</p>
        <div class="type-options">
          <button class="type-btn product" @click="selectType('PRODUCT')">产品类目</button>
          <button class="type-btn news" @click="selectType('NEWS')">新闻类目</button>
        </div>
        <div class="actions">
          <button @click="typeSelectOpen = false" class="btn-cancel">取消</button>
        </div>
      </div>
    </div>

    <div v-if="paramManagerOpen" class="modal-overlay" @click.self="closeParamManager">
      <div class="modal param-modal">
        <div class="param-modal-header">
          <h2>产品参数设置</h2>
          <button @click="openParamEditor()" class="btn-add-param">+ 添加参数</button>
        </div>

        <div class="param-table">
          <table>
            <thead>
              <tr>
                <th>序号</th>
                <th>参数名称</th>
                <th>单位</th>
                <th>启用/停用</th>
                <th>分配类目</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(p, idx) in paramsList" :key="p.id">
                <td>{{ idx + 1 }}</td>
                <td class="param-name">{{ p.name }}</td>
                <td>{{ p.unit || '—' }}</td>
                <td>
                  <label class="toggle" :title="p.enabled ? '点击停用' : '点击启用'">
                    <input type="checkbox" :checked="p.enabled" @change="onEnableChange(p, $event)" />
                    <span class="slider"></span>
                  </label>
                </td>
                <td>
                  <button @click="openAssignModal(p)" class="btn-assign" :disabled="!p.enabled">分配</button>
                </td>
                <td class="param-actions">
                  <button @click="openParamEditor(p)" class="btn-edit-sm">编辑</button>
                  <button @click="deleteParam(p.id)" class="btn-del-sm">删除</button>
                </td>
              </tr>
              <tr v-if="paramsList.length === 0">
                <td colspan="6" class="empty">暂无参数，点击右上角「+ 添加参数」</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="param-modal-footer">
          <button @click="closeParamManager" class="btn-back">返回</button>
        </div>
      </div>
    </div>

    <div v-if="assignModalOpen" class="modal-overlay" @click.self="assignModalOpen = false">
      <div class="modal" style="width:520px;">
        <h2>分配参数到类目</h2>
        <p class="assign-hint">选择要应用参数「{{ assigningParam?.name }}」的产品类目：</p>
        <div class="assign-list">
          <label class="assign-row">
            <input type="checkbox" :checked="allAssigned" @change="toggleAll" />
            <span>全选</span>
          </label>
          <label v-for="cat in productCats" :key="cat.id" class="assign-row">
            <input type="checkbox" :value="cat.id" v-model="selectedCats" />
            <span>{{ cat.nameEn }}</span>
          </label>
        </div>
        <div class="actions" style="margin-top:16px;">
          <button @click="saveAssign" class="btn-save" :disabled="savingAssign">{{ savingAssign ? '保存中...' : '保存' }}</button>
          <button @click="assignModalOpen = false" class="btn-cancel">取消</button>
        </div>
      </div>
    </div>

    <div v-if="paramEditorOpen" class="modal-overlay" @click.self="paramEditorOpen = false">
      <div class="modal" style="width:420px;">
        <h2>{{ editingParam?.id ? '编辑参数' : '添加参数' }}</h2>
        <div class="field"><label>参数名称 *</label><input v-model="paramForm.name" placeholder="如：Mesh Size" /></div>
        <div class="field"><label>单位</label><input v-model="paramForm.unit" placeholder="如：mm" /></div>
        <div class="field">
          <label>状态</label>
          <label class="toggle">
            <input type="checkbox" v-model="paramForm.enabled" />
            <span class="slider"></span>
          </label>
        </div>
        <div v-if="paramError" class="save-error">{{ paramError }}</div>
        <div v-if="paramSuccess" class="save-success">{{ paramSuccess }}</div>
        <div class="actions">
          <button @click="saveParam" class="btn-save">{{ savingParam ? '保存中...' : '保存' }}</button>
          <button @click="paramEditorOpen = false" class="btn-cancel">取消</button>
        </div>
      </div>
    </div>

    <div v-if="disableConfirmOpen" class="modal-overlay" @click.self="cancelDisable">
      <div class="modal warning-modal">
        <h2>确认停用参数</h2>
        <p class="warning-text">停用参数「{{ disableTarget?.name }}」后，以下类目将不再能设置该参数：</p>
        <ul class="warning-list">
          <li v-for="cat in disableTarget?.assignedCats || []" :key="cat.id">{{ cat.nameEn }}</li>
        </ul>
        <p class="warning-note">停用后，已分配给该类目的参数也会被取消分配。</p>
        <div class="actions">
          <button @click="confirmDisable" class="btn-confirm-disable">确认停用</button>
          <button @click="cancelDisable" class="btn-cancel">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { api } from '../../api'

const categories = ref<any[]>([])
const type = ref<'PRODUCT' | 'NEWS'>('PRODUCT')
const modalOpen = ref(false)
const typeSelectOpen = ref(false)
const editing = ref<any>(null)
const saving = ref(false)
const saveError = ref('')
const saveSuccess = ref('')
const form = ref({ nameEn: '', sort: 0, description: '', imageUrl: '' })
const categoryParams = ref<any[]>([])
const draggedParamIndex = ref<number | null>(null)
const dragOverParamIndex = ref<number | null>(null)

const paramManagerOpen = ref(false)
const paramsList = ref<any[]>([])
const paramEditorOpen = ref(false)
const editingParam = ref<any>(null)
const paramForm = ref({ name: '', unit: '', enabled: true })
const paramError = ref('')
const paramSuccess = ref('')
const savingParam = ref(false)

const assignModalOpen = ref(false)
const assigningParam = ref<any>(null)
const productCats = ref<any[]>([])
const selectedCats = ref<number[]>([])
const savingAssign = ref(false)

const disableConfirmOpen = ref(false)
const disableTarget = ref<any>(null)

const allAssigned = computed(
  () => productCats.value.length > 0 && selectedCats.value.length === productCats.value.length,
)

async function load() {
  categories.value = await api.categories.listByType(type.value)
}

function switchType(nextType: 'PRODUCT' | 'NEWS') {
  type.value = nextType
  load()
}

function openTypeSelect() {
  typeSelectOpen.value = true
}

async function openModal(cat?: any) {
  let currentCat = cat
  if (currentCat?.id) {
    const fresh = await api.categories.listByType(type.value)
    currentCat = fresh.find((c: any) => c.id === currentCat.id) ?? currentCat
  }
  editing.value = currentCat ?? null
  form.value = currentCat
    ? {
        nameEn: currentCat.nameEn,
        sort: currentCat.sort ?? 0,
        description: currentCat.description ?? '',
        imageUrl: currentCat.imageUrl ?? '',
      }
    : { nameEn: '', sort: 0, description: '', imageUrl: '' }
  categoryParams.value = currentCat && type.value === 'PRODUCT' ? parseCategoryParams(currentCat.specs) : []
  endParamDrag()
  saveError.value = ''
  saveSuccess.value = ''
  modalOpen.value = true
}

function selectType(nextType: 'PRODUCT' | 'NEWS') {
  type.value = nextType
  load()
  typeSelectOpen.value = false
  modalOpen.value = true
}

function closeModal() {
  modalOpen.value = false
  endParamDrag()
}

function parseCategoryParams(specs: any): any[] {
  if (!specs) return []
  if (Array.isArray(specs)) return [...specs]
  try {
    const parsed = JSON.parse(specs)
    return Array.isArray(parsed) ? [...parsed] : []
  } catch {
    return []
  }
}

function startParamDrag(index: number, event: DragEvent) {
  draggedParamIndex.value = index
  dragOverParamIndex.value = index
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('text/plain', String(index))
  }
}

function setParamDragOver(index: number) {
  if (draggedParamIndex.value !== null && draggedParamIndex.value !== index) {
    dragOverParamIndex.value = index
  }
}

function dropParam(targetIndex: number) {
  const sourceIndex = draggedParamIndex.value
  if (sourceIndex === null || sourceIndex === targetIndex) {
    endParamDrag()
    return
  }
  const reordered = [...categoryParams.value]
  const [moved] = reordered.splice(sourceIndex, 1)
  reordered.splice(targetIndex, 0, moved)
  categoryParams.value = reordered
  endParamDrag()
}

function endParamDrag() {
  draggedParamIndex.value = null
  dragOverParamIndex.value = null
}

async function save() {
  saving.value = true
  saveError.value = ''
  saveSuccess.value = ''
  try {
    const payload: any = { ...form.value, type: type.value }
    if (type.value === 'PRODUCT') {
      payload.specs = JSON.stringify(categoryParams.value)
    }
    if (editing.value?.id) await api.categories.update(editing.value.id, payload)
    else await api.categories.create(payload)
    saveSuccess.value = '已保存'
    await load()
    setTimeout(closeModal, 600)
  } catch (err: any) {
    saveError.value = '保存失败: ' + err.message
  } finally {
    saving.value = false
  }
}

function requestDelete(cat: any) {
  if (confirm('确定删除类目「' + cat.nameEn + '」吗？')) {
    api.categories.delete(cat.id).then(load)
  }
}

async function onFileSelect(event: Event, field: 'imageUrl') {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return
  try {
    const res = await api.upload.image(file)
    form.value[field] = res.url
  } catch (err: any) {
    alert('上传失败: ' + err.message)
  }
}

async function openParamManager() {
  paramManagerOpen.value = true
  paramError.value = ''
  await loadParams()
}

function closeParamManager() {
  paramManagerOpen.value = false
}

async function loadParams() {
  try {
    paramsList.value = await api.categories.params.listAll()
  } catch (err: any) {
    paramError.value = '参数加载失败: ' + err.message
  }
}

function openParamEditor(param?: any) {
  editingParam.value = param ?? null
  paramForm.value = param
    ? { name: param.name, unit: param.unit ?? '', enabled: param.enabled ?? true }
    : { name: '', unit: '', enabled: true }
  paramError.value = ''
  paramSuccess.value = ''
  paramEditorOpen.value = true
}

async function saveParam() {
  const name = paramForm.value.name.trim()
  const unit = paramForm.value.unit.trim()
  if (!name) {
    paramError.value = '参数名称不能为空'
    return
  }

  savingParam.value = true
  paramError.value = ''
  paramSuccess.value = ''
  try {
    const payload = { name, unit, enabled: paramForm.value.enabled }
    if (editingParam.value?.id) {
      const updated = await api.categories.params.update(editingParam.value.id, payload)
      const index = paramsList.value.findIndex((item: any) => item.id === editingParam.value.id)
      if (index >= 0) paramsList.value[index] = updated
      paramSuccess.value = '已更新'
    } else {
      const created = await api.categories.params.create(payload)
      paramsList.value.push(created)
      paramSuccess.value = '已添加'
    }
    setTimeout(() => {
      paramEditorOpen.value = false
    }, 600)
  } catch (err: any) {
    paramError.value = '保存失败: ' + err.message
  } finally {
    savingParam.value = false
  }
}

async function deleteParam(id: number) {
  if (!confirm('确定删除该参数吗？')) return
  try {
    await api.categories.params.delete(id)
    paramsList.value = paramsList.value.filter((item: any) => item.id !== id)
  } catch (err: any) {
    alert('删除失败: ' + err.message)
  }
}

async function onEnableChange(param: any, event: Event) {
  const target = event.target as HTMLInputElement

  if (target.checked) {
    try {
      const updated = await api.categories.params.toggle(param.id)
      Object.assign(param, updated)
    } catch (err: any) {
      target.checked = false
      alert('操作失败: ' + err.message)
    }
    return
  }

  try {
    const assignedCats = await api.categories.params.getAssignedCategories(param.id)
    if (assignedCats.length > 0) {
      disableTarget.value = { ...param, assignedCats }
      disableConfirmOpen.value = true
      target.checked = true
      return
    }

    const updated = await api.categories.params.toggle(param.id)
    Object.assign(param, updated)
  } catch (err: any) {
    target.checked = true
    alert('操作失败: ' + err.message)
  }
}

async function confirmDisable() {
  if (!disableTarget.value) return
  try {
    const updated = await api.categories.params.toggle(disableTarget.value.id)
    const param = paramsList.value.find((item: any) => item.id === disableTarget.value.id)
    if (param) Object.assign(param, updated)
    cancelDisable()
  } catch (err: any) {
    alert('操作失败: ' + err.message)
  }
}

function cancelDisable() {
  disableConfirmOpen.value = false
  disableTarget.value = null
}

async function openAssignModal(param: any) {
  assigningParam.value = param
  selectedCats.value = []
  assignModalOpen.value = true
  await loadProductCats(param)
}

async function loadProductCats(param: any) {
  try {
    productCats.value = await api.categories.listByType('PRODUCT')
    if (param.assignedCategoryIds) {
      const ids = JSON.parse(param.assignedCategoryIds)
      selectedCats.value = Array.isArray(ids) ? ids : []
    }
  } catch (err: any) {
    alert('类目加载失败: ' + err.message)
  }
}

function toggleAll(event: Event) {
  const checked = (event.target as HTMLInputElement).checked
  selectedCats.value = checked ? productCats.value.map((cat: any) => cat.id) : []
}

async function saveAssign() {
  if (!assigningParam.value) return
  savingAssign.value = true
  try {
    const payload = {
      ...assigningParam.value,
      assignedCategoryIds: JSON.stringify(selectedCats.value),
    }
    const updated = await api.categories.params.update(assigningParam.value.id, payload)
    const param = paramsList.value.find((item: any) => item.id === assigningParam.value.id)
    if (param) Object.assign(param, updated)
    assignModalOpen.value = false
  } catch (err: any) {
    alert('保存失败: ' + err.message)
  } finally {
    savingAssign.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
h1 { font-size: 22px; }
.btn-add {
  background: var(--yq-red);
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}
.btn-param {
  background: #1677ff;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  margin-right: 8px;
}
.tabs { display: flex; gap: 8px; margin-bottom: 16px; }
.tabs button {
  padding: 7px 18px;
  border: 1px solid var(--yq-gray-200);
  border-radius: 4px;
  background: #fff;
  font-size: 13px;
  cursor: pointer;
}
.tabs button.active {
  background: var(--yq-red);
  color: #fff;
  border-color: var(--yq-red);
}
.list {
  background: #fff;
  border: 1px solid var(--yq-gray-200);
  border-radius: 6px;
  padding: 8px;
}
.row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 4px;
  font-size: 14px;
}
.row:hover { background: var(--yq-gray-50); }
.thumb {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
  flex-shrink: 0;
}
.thumb.empty { background: var(--yq-gray-200); }
.name { flex: 1; font-weight: 600; }
.meta { font-size: 12px; color: #999; }
.btn-edit,
.btn-del {
  padding: 4px 10px;
  border: 1px solid var(--yq-gray-200);
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  background: #fff;
  margin-right: 4px;
}
.btn-del { color: red; border-color: #fcc; }
.empty { text-align: center; color: #999; padding: 24px; }
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}
.modal {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  width: 480px;
  max-width: calc(100vw - 32px);
}
.modal h2 { margin-bottom: 16px; }
.field { margin-bottom: 12px; }
.field label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 4px;
}
.field input,
.field textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid var(--yq-gray-200);
  border-radius: 4px;
  font-size: 14px;
}
.param-order-list {
  overflow: hidden;
  border: 1px solid var(--yq-gray-200);
  border-radius: 4px;
  background: #fff;
}
.param-order-row {
  display: grid;
  grid-template-columns: 24px 28px minmax(0, 1fr) auto;
  align-items: center;
  gap: 8px;
  padding: 9px 10px;
  border-bottom: 1px solid var(--yq-gray-100);
  cursor: grab;
  user-select: none;
}
.param-order-row:last-child { border-bottom: none; }
.param-order-row:hover { background: var(--yq-gray-50); }
.param-order-row.dragging { opacity: 0.35; }
.param-order-row.drag-over {
  box-shadow: inset 0 2px 0 var(--yq-red);
  background: #fff7f6;
}
.drag-handle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #9aa0a6;
}
.drag-icon { width: 16px; height: 16px; }
.param-order-index {
  color: #9aa0a6;
  font-size: 12px;
  text-align: center;
}
.param-order-name {
  overflow: hidden;
  color: var(--yq-gray-800);
  font-size: 13px;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.param-order-unit { color: #888; font-size: 12px; }
.param-order-empty {
  padding: 14px;
  border: 1px dashed var(--yq-gray-200);
  border-radius: 4px;
  color: #999;
  font-size: 13px;
  text-align: center;
}
.preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  margin-top: 8px;
  border: 1px solid #ddd;
}
.save-error { color: red; font-size: 13px; margin-bottom: 12px; }
.save-success { color: #155724; font-size: 13px; margin-bottom: 12px; }
.actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 16px; }
.btn-save,
.btn-confirm-disable {
  background: var(--yq-red);
  color: #fff;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
}
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-cancel {
  background: var(--yq-gray-200);
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.param-modal {
  width: 640px;
  height: 800px;
  max-width: calc(100vw - 32px);
  max-height: calc(100vh - 32px);
  display: flex;
  flex-direction: column;
}
.param-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.param-modal-header h2 { margin: 0; font-size: 18px; }
.btn-add-param {
  background: var(--yq-red);
  color: #fff;
  border: none;
  padding: 7px 14px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
}
.param-table {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  border: 1px solid var(--yq-gray-200);
  border-radius: 4px;
  margin-bottom: 16px;
}
.param-table table { width: 100%; border-collapse: collapse; table-layout: fixed; }
.param-table th {
  position: sticky;
  top: 0;
  text-align: left;
  padding: 10px 8px;
  border-bottom: 2px solid var(--yq-gray-200);
  font-size: 12px;
  color: #888;
  background: #fafafa;
}
.param-table td {
  padding: 10px 8px;
  border-bottom: 1px solid var(--yq-gray-100);
  font-size: 13px;
  vertical-align: middle;
}
.param-table th:nth-child(1),
.param-table td:nth-child(1) { width: 52px; }
.param-table th:nth-child(3),
.param-table td:nth-child(3) { width: 72px; }
.param-table th:nth-child(4),
.param-table td:nth-child(4) { width: 82px; }
.param-table th:nth-child(5),
.param-table td:nth-child(5) { width: 82px; }
.param-table th:nth-child(6),
.param-table td:nth-child(6) { width: 122px; }
.param-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 600;
}
.param-actions { white-space: nowrap; }
.param-table tr:last-child td { border-bottom: none; }
.btn-assign {
  padding: 4px 10px;
  border: 1px solid #1677ff;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  background: #fff;
  color: #1677ff;
}
.btn-assign:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  color: #ccc;
  border-color: #ccc;
}
.btn-edit-sm {
  padding: 4px 8px;
  border: 1px solid var(--yq-gray-200);
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  background: #fff;
  margin-right: 4px;
}
.btn-del-sm {
  padding: 4px 8px;
  border: 1px solid #fcc;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  color: red;
  background: #fff;
}
.toggle { display: inline-block; cursor: pointer; }
.toggle input { display: none; }
.slider {
  display: block;
  width: 36px;
  height: 20px;
  background: #ccc;
  border-radius: 10px;
  position: relative;
  transition: 0.3s;
}
.slider::before {
  content: '';
  position: absolute;
  width: 16px;
  height: 16px;
  background: #fff;
  border-radius: 50%;
  top: 2px;
  left: 2px;
  transition: 0.3s;
}
.toggle input:checked + .slider { background: #52c41a; }
.toggle input:checked + .slider::before { transform: translateX(16px); }
.param-modal-footer { display: flex; justify-content: flex-end; }
.btn-back {
  background: var(--yq-gray-200);
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.assign-hint { color: #555; font-size: 13px; margin-bottom: 12px; }
.assign-list {
  max-height: 240px;
  overflow-y: auto;
  border: 1px solid var(--yq-gray-200);
  border-radius: 4px;
  padding: 8px;
}
.assign-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 4px;
  font-size: 13px;
  cursor: pointer;
}
.assign-row input[type='checkbox'] {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: var(--yq-red);
  flex-shrink: 0;
}

.warning-modal { width: 440px; }
.warning-text { color: #d4380d; font-size: 14px; margin-bottom: 10px; }
.warning-list {
  padding: 10px 10px 10px 30px;
  color: #555;
  font-size: 13px;
  margin-bottom: 10px;
  background: #fff7f5;
  border-radius: 4px;
}
.warning-note { color: #888; font-size: 12px; line-height: 1.6; }
.type-select-modal { width: 400px; text-align: center; }
.type-hint { color: #666; font-size: 13px; margin-bottom: 20px; }
.type-options { display: flex; gap: 12px; justify-content: center; margin: 20px 0; }
.type-btn {
  flex: 1;
  padding: 16px 0;
  border: 2px solid var(--yq-gray-200);
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  color: var(--yq-text);
  transition: all 0.15s;
}
.type-btn:hover { border-color: var(--yq-red); color: var(--yq-red); }
.type-btn.product:hover { border-color: #2563eb; color: #2563eb; }
.type-btn.news:hover { border-color: #16a34a; color: #16a34a; }
\n
.type-select-modal { width: 400px; text-align: center; }
.type-hint { color: #666; font-size: 13px; margin-bottom: 20px; }
.type-options { display: flex; gap: 12px; justify-content: center; margin: 20px 0; }
.type-btn {
  flex: 1;
  padding: 16px 0;
  border: 2px solid var(--yq-gray-200);
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  color: var(--yq-text);
  transition: all 0.15s;
}
.type-btn:hover { border-color: var(--yq-red); color: var(--yq-red); }
.type-btn.product:hover { border-color: #2563eb; color: #2563eb; }
.type-btn.news:hover { border-color: #16a34a; color: #16a34a; }
</style>
