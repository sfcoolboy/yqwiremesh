<template>
  <div>
    <div class="header"><h1>Products</h1><button @click="openModal()" class="btn-add">+ Add Product</button></div>
    <table class="data-table">
      <thead><tr><th>Name</th><th>Category</th><th>Sort</th><th>Actions</th></tr></thead>
      <tbody>
        <tr v-for="p in products" :key="p.id">
          <td>{{ p.name }}</td>
          <td>{{ categoryName(p.categoryId) }}</td>
          <td>{{ p.sort }}</td>
          <td><button @click="openModal(p)" class="btn-edit">Edit</button> <button @click="del(p.id)" class="btn-del">Del</button></td>
        </tr>
        <tr v-if="!products.length"><td colspan="4" class="empty">No products yet</td></tr>
      </tbody>
    </table>
    <div v-if="modalOpen" class="modal-overlay" @click.self="modalOpen = false">
      <div class="modal">
        <h2>{{ editing?.id ? 'Edit' : 'Add' }} Product</h2>
        <div class="field"><label>Name *</label><input v-model="form.name" /></div>
        <div class="field"><label>Category *</label>
          <select v-model.number="form.categoryId">
            <option :value="null">Select...</option>
            <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.nameEn }}</option>
          </select>
        </div>
        <div class="field"><label>Sort Order</label><input v-model.number="form.sort" type="number" /></div>
        <div class="field"><label>Description</label><textarea v-model="form.description" rows="4" /></div>
        <div class="field"><label>Image URL</label><input v-model="form.imageUrl" /></div>
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

const products = ref<any[]>([])
const categories = ref<any[]>([])
const modalOpen = ref(false)
const editing = ref<any>(null)
const form = ref({ name: '', categoryId: null as number | null, sort: 0, description: '', imageUrl: '' })

const catMap = ref(new Map<number, string>())
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
  form.value = p ? { name: p.name, categoryId: p.categoryId, sort: p.sort ?? 0, description: p.description ?? '', imageUrl: p.imageUrl ?? '' }
    : { name: '', categoryId: null, sort: 0, description: '', imageUrl: '' }
  modalOpen.value = true
}

async function save() {
  if (editing.value?.id) await api.products.update(editing.value.id, form.value)
  else await api.products.create(form.value)
  modalOpen.value = false
  await load()
}

async function del(id: number) {
  if (!confirm('Delete this product?')) return
  await api.products.delete(id)
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
.btn-edit, .btn-del { padding: 4px 10px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 12px; cursor: pointer; background: #fff; margin-right: 4px; }
.btn-del { color: red; border-color: #fcc; }
.empty { text-align: center; color: #999; padding: 20px !important; }
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
