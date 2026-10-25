<template>
  <div>
    <div class="header"><h1>News</h1><button @click="openModal()" class="btn-add">+ Add News</button></div>
    <div class="list">
      <div v-for="n in news" :key="n.id" class="news-item">
        <div class="thumb" v-if="n.coverImageUrl" :style="{ backgroundImage: 'url(' + n.coverImageUrl + ')' }" />
        <div class="thumb empty" v-else />
        <div class="info">
          <span class="tag">{{ n.category }}</span>
          <h4>{{ n.title }}</h4>
          <span class="date">{{ formatDate(n.publishDate) }}</span>
        </div>
        <div class="actions">
          <button @click="openModal(n)" class="btn-edit">Edit</button>
          <button @click="del(n.id)" class="btn-del">Del</button>
        </div>
      </div>
      <div v-if="!news.length" class="empty">No news yet</div>
    </div>
    <div v-if="modalOpen" class="modal-overlay" @click.self="modalOpen = false">
      <div class="modal">
        <h2>{{ editing?.id ? 'Edit' : 'Add' }} News</h2>
        <div class="field"><label>Title *</label><input v-model="form.title" /></div>
        <div class="field"><label>Summary</label><input v-model="form.summary" /></div>
        <div class="field"><label>Category</label>
          <select v-model="form.category">
            <option>COMPANY_NEWS</option><option>INDUSTRY_NEWS</option><option>EXHIBITION</option>
          </select>
        </div>
        <div class="field"><label>Publish Date</label><input v-model="form.publishDate" type="date" /></div>
        <div class="field"><label>Cover Image URL</label><input v-model="form.coverImageUrl" /></div>
        <div class="field"><label>Content (HTML)</label><textarea v-model="form.content" rows="8" /></div>
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

const news = ref<any[]>([])
const modalOpen = ref(false)
const editing = ref<any>(null)
const form = ref({ title: '', summary: '', category: 'COMPANY_NEWS', publishDate: '', coverImageUrl: '', content: '' })

async function load() { news.value = await api.news.list() }

function openModal(n?: any) {
  editing.value = n
  form.value = n ? { title: n.title, summary: n.summary ?? '', category: n.category, publishDate: n.publishDate?.slice(0,10) ?? '', coverImageUrl: n.coverImageUrl ?? '', content: n.content ?? '' }
    : { title: '', summary: '', category: 'COMPANY_NEWS', publishDate: new Date().toISOString().slice(0,10), coverImageUrl: '', content: '' }
  modalOpen.value = true
}

async function save() {
  const payload = { ...form.value, publishDate: form.value.publishDate + 'T00:00:00' }
  if (editing.value?.id) await api.news.update(editing.value.id, payload)
  else await api.news.create(payload)
  modalOpen.value = false
  await load()
}

async function del(id: number) {
  if (!confirm('Delete this news?')) return
  await api.news.delete(id)
  await load()
}

function formatDate(d: string) { return new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' }) }
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
.btn-save { background: var(--yq-red); color: #fff; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.btn-cancel { background: var(--yq-gray-200); border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
</style>