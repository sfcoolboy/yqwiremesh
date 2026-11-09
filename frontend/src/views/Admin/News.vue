<template>
  <div>
    <div class="header"><h1>新闻动态</h1><button @click="openModal()" class="btn-add">+ 添加新闻</button></div>
    <div class="list">
      <div v-for="n in news" :key="n.id" class="news-item">
        <div class="thumb" v-if="n.coverImageUrl" :style="{ backgroundImage: 'url(' + n.coverImageUrl + ')' }" />
        <div class="thumb empty" v-else />
        <div class="info">
          <span class="tag">{{ categoryLabel(n.category) }}</span>
          <h4>{{ n.title }}</h4>
          <span class="date">{{ formatDate(n.publishDate) }}</span>
        </div>
        <div class="actions">
          <button @click="openModal(n)" class="btn-edit">编辑</button>
          <button @click="requestDelete(n)" class="btn-del">删除</button>
        </div>
      </div>
      <div v-if="!news.length" class="empty">暂无新闻</div>
    </div>
    <div v-if="modalOpen" class="modal-overlay">
      <div class="modal">
        <h2>{{ editing?.id ? '编辑新闻' : '添加新闻' }}</h2>
        <div class="field"><label>标题 *</label><input v-model="form.title" /></div>
        <div class="field"><label>摘要</label><input v-model="form.summary" /></div>
        <div class="field"><label>分类</label>
          <select v-model="form.category">
            <option value="">请选择...</option>
            <option v-for="c in newsCategories" :key="c.id" :value="c.nameEn">{{ c.nameEn }}</option>
          </select>
        </div>
        <div class="field"><label>来源</label><input v-model="form.source" placeholder="如：转载自某某网站（选填）" /></div>
        <div class="field"><label>发布日期</label><input v-model="form.publishDate" type="date" /></div>
        <div class="field">
          <label>封面图片</label>
          <input type="file" accept="image/*" @change="onFileSelect($event, 'coverImageUrl')" />
          <img v-if="form.coverImageUrl" :src="form.coverImageUrl" class="preview" />
          <span class="hint-text">建议尺寸 2:1（如 800×400）</span>
        </div>
        <div class="field">
          <label>正文内容</label>
          <div class="editor-wrapper">
            <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" mode="default" class="editor-toolbar" />
            <Editor
              v-model="form.content"
              :defaultConfig="editorConfig"
              mode="default"
              class="editor-body"
              @onCreated="handleEditorCreated"
            />
          </div>
        </div>
        <div v-if="saveError" class="save-error">{{ saveError }}</div>
        <div class="actions">
          <button @click="save" class="btn-save">{{ saving ? '保存中...' : '保存' }}</button>
          <button @click="modalOpen = false" class="btn-cancel">取消</button>
        </div>
      </div>
    </div>
    <ConfirmDialog
      :visible="!!deleteTarget"
      title="删除新闻"
      :message="`确定要删除新闻「${deleteTarget?.title || ''}」吗？删除后不可恢复。`"
      :loading="deleting"
      @cancel="deleteTarget = null"
      @confirm="doDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, shallowRef, onMounted, onBeforeUnmount } from 'vue'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { api } from '../../api'
import ConfirmDialog from '../../components/ConfirmDialog.vue'

const news = ref<any[]>([])
const modalOpen = ref(false)
const editing = ref<any>(null)
const saving = ref(false)
const saveError = ref('')
const deleteTarget = ref<any>(null)
const deleting = ref(false)
const newsCategories = ref<any[]>([])
const form = ref({ title: '', summary: '', category: '', source: '', publishDate: '', coverImageUrl: '', content: '' })

const editorRef = shallowRef<any>()
const toolbarConfig = { excludeKeys: ['group-video'] }
const editorConfig = {
  placeholder: '请输入新闻正文...',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file: File, insertFn: (url: string, alt?: string, href?: string) => void) {
        try {
          const res = await api.upload.image(file)
          insertFn(res.url, '', res.url)
        } catch (e: any) {
          alert('图片上传失败: ' + e.message)
        }
      },
    },
  },
}

function handleEditorCreated(editor: any) {
  editorRef.value = editor
}

onBeforeUnmount(() => {
  editorRef.value?.destroy()
})

async function load() {
  news.value = await api.news.list()
  newsCategories.value = await api.categories.listByType('NEWS')
}

function openModal(n?: any) {
  editing.value = n
  form.value = n ? { title: n.title, summary: n.summary ?? '', category: n.category, source: n.source ?? '', publishDate: n.publishDate?.slice(0,10) ?? '', coverImageUrl: n.coverImageUrl ?? '', content: n.content ?? '' }
    : { title: '', summary: '', category: newsCategories.value[0]?.nameEn ?? '', source: '', publishDate: new Date().toISOString().slice(0,10), coverImageUrl: '', content: '' }
  saveError.value = ''
  modalOpen.value = true
}

async function onFileSelect(e: Event, field: string) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  try {
    const res = await api.upload.image(file)
    form.value[field] = res.url
  } catch (err: any) {
    alert('上传失败: ' + err.message)
  }
}

async function save() {
  saving.value = true
  saveError.value = ''
  try {
    const payload = { ...form.value, publishDate: form.value.publishDate + 'T00:00:00' }
    if (editing.value?.id) await api.news.update(editing.value.id, payload)
    else await api.news.create(payload)
    modalOpen.value = false
    await load()
  } catch (err: any) {
    saveError.value = '保存失败: ' + err.message
  } finally {
    saving.value = false
  }
}

function requestDelete(n: any) {
  deleteTarget.value = n
}

async function doDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await api.news.delete(deleteTarget.value.id)
    deleteTarget.value = null
    await load()
  } catch (err: any) {
    alert('删除失败: ' + err.message)
  } finally {
    deleting.value = false
  }
}

function formatDate(d: string) { return new Date(d).toLocaleDateString('zh-CN') }
function categoryLabel(c: string) {
  return c || '未分类'
}
onMounted(load)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
h1 { font-size: 22px; }
.btn-add { background: var(--yq-red); color: #fff; border: none; padding: 8px 16px; border-radius: 4px; font-size: 13px; cursor: pointer; }
.list { display: flex; flex-direction: column; gap: 10px; }
.news-item { display: flex; align-items: center; gap: 12px; background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 12px; }
.thumb { width: 80px; height: 50px; background: var(--yq-gray-200); background-size: cover; background-position: center; border-radius: 4px; flex-shrink: 0; }
.thumb.empty { background: repeating-linear-gradient(45deg, var(--yq-gray-100), var(--yq-gray-100) 10px, var(--yq-gray-200) 10px, var(--yq-gray-200) 20px); }
.info { flex: 1; }
.tag { font-size: 11px; text-transform: uppercase; color: var(--yq-red); font-weight: 600; }
.info h4 { font-size: 14px; margin: 4px 0; }
.date { font-size: 12px; color: #999; }
.actions { display: flex; gap: 6px; }
.btn-edit, .btn-del { padding: 4px 10px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 12px; cursor: pointer; background: #fff; }
.btn-del { color: red; border-color: #fcc; }
.empty { text-align: center; color: #999; padding: 20px; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; padding: 24px; border-radius: 8px; width: 520px; max-width: 90vw; max-height: 90vh; overflow-y: auto; }
.modal h2 { margin-bottom: 16px; }
.field { margin-bottom: 12px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 4px; }
.field input, .field select, .field textarea { width: 100%; padding: 8px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 14px; }
.preview { width: 120px; height: 80px; object-fit: cover; border-radius: 4px; margin-top: 8px; border: 1px solid #ddd; }
.editor-wrapper { border: 1px solid var(--yq-gray-200); border-radius: 4px; overflow: hidden; }
.editor-toolbar { border-bottom: 1px solid var(--yq-gray-200); }
.editor-body { height: 320px; overflow-y: auto; }
.save-error { color: red; font-size: 13px; margin-bottom: 12px; }
.actions { display: flex; gap: 8px; margin-top: 16px; }
.btn-save { background: var(--yq-red); color: #fff; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.btn-cancel { background: var(--yq-gray-200); border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.hint-text { font-size: 12px; color: #999; margin-top: 4px; display: block; }
</style>
