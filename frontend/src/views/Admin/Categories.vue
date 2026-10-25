<template>
  <div>
    <div class="header"><h1>Categories</h1><button @click="openModal(null)" class="btn-add">+ Add Category</button></div>
    <div class="tree">
      <div v-for="cat in categories" :key="cat.id" class="tree-item">
        <div class="tree-row">
          <span class="dot" />
          <strong>{{ cat.nameEn }}</strong>
          <span class="meta">Sort: {{ cat.sort }}</span>
          <button @click="openModal(cat)" class="btn-edit">Edit</button>
          <button @click="del(cat.id)" class="btn-del">Del</button>
        </div>
        <div v-if="cat.children?.length" class="children">
          <div v-for="sc in cat.children" :key="sc.id" class="tree-item child">
            <div class="tree-row">
              <span class="dot sub" />
              <span>{{ sc.nameEn }}</span>
              <span class="meta">Sort: {{ sc.sort }}</span>
              <button @click="openModal(sc, cat.id)" class="btn-edit">Edit</button>
              <button @click="del(sc.id)" class="btn-del">Del</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="modalOpen" class="modal-overlay" @click.self="modalOpen = false">
      <div class="modal">
        <h2>{{ editing?.id ? 'Edit' : 'Add' }} Category</h2>
        <div class="field"><label>Name (English) *</label><input v-model="form.nameEn" /></div>
        <div class="field"><label>Parent Category</label>
          <select v-model="form.parentId">
            <option :value="null">Root (Top-level)</option>
            <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.nameEn }}</option>
          </select>
        </div>
        <div class="field"><label>Sort Order</label><input v-model.number="form.sort" type="number" /></div>
        <div class="field"><label>Description</label><textarea v-model="form.description" rows="3" /></div>
        <div class="field"><label>Image URL</label><input v-model="form.imageUrl" placeholder="https://..." /></div>
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

const categories = ref<any[]>([])
const modalOpen = ref(false)
const editing = ref<any>(null)
const form = ref({ nameEn: '', parentId: null as number | null, sort: 0, description: '', imageUrl: '' })

async function load() { categories.value = await api.categories.tree() }

function openModal(cat: any, parentId?: number) {
  editing.value = cat
  form.value = cat ? { nameEn: cat.nameEn, parentId: cat.parentId ?? null, sort: cat.sort ?? 0, description: cat.description ?? '', imageUrl: cat.imageUrl ?? '' }
    : { nameEn: '', parentId: parentId ?? null, sort: 0, description: '', imageUrl: '' }
  modalOpen.value = true
}

async function save() {
  if (editing.value?.id) await api.categories.update(editing.value.id, form.value)
  else await api.categories.create(form.value)
  modalOpen.value = false
  await load()
}

async function del(id: number) {
  if (!confirm('Delete this category?')) return
  await api.categories.delete(id)
  await load()
}

onMounted(load)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
h1 { font-size: 22px; }
.btn-add { background: var(--yq-red); color: #fff; border: none; padding: 8px 16px; border-radius: 4px; font-size: 13px; cursor: pointer; }
.tree { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 16px; }
.tree-item { margin-bottom: 4px; }
.tree-row { display: flex; align-items: center; gap: 12px; padding: 8px 12px; background: var(--yq-gray-50); border-radius: 4px; font-size: 14px; }
.tree-row.child { margin-left: 24px; background: #fafafa; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: var(--yq-red); flex-shrink: 0; }
.dot.sub { background: var(--yq-gray-400); }
.meta { font-size: 12px; color: #999; }
.btn-edit, .btn-del { padding: 4px 10px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 12px; cursor: pointer; background: #fff; }
.btn-del { color: red; border-color: #fcc; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; padding: 24px; border-radius: 8px; width: 420px; max-width: 90vw; }
.modal h2 { margin-bottom: 16px; }
.field { margin-bottom: 12px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 4px; }
.field input, .field select, .field textarea { width: 100%; padding: 8px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 14px; }
.actions { display: flex; gap: 8px; margin-top: 16px; }
.btn-save { background: var(--yq-red); color: #fff; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.btn-cancel { background: var(--yq-gray-200); border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
</style>

