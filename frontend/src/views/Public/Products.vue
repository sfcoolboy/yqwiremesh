<template>
  <div>
    <PublicHeader />
    <div class="page-head">
      <h1>Products</h1>
      <p>Explore our full range of metal grating, wire mesh, and guardrail solutions.</p>
    </div>
    <div class="container section">
      <div class="products-layout">
        <!-- Sidebar categories -->
        <aside class="sidebar">
          <h3>Categories</h3>
          <ul>
            <li v-for="cat in categories" :key="cat.id" @click="selectCategory(cat)">
              <span class="cat-name">{{ cat.nameEn }}</span>
              <span class="cat-count">{{ cat.children?.length ?? 0 }}</span>
            </li>
          </ul>
        </aside>

        <!-- Main product area -->
        <main class="main">
          <div v-if="selectedCat" class="selected-header">
            <h2>{{ selectedCat.nameEn }}</h2>
            <p v-if="selectedCat.description">{{ selectedCat.description }}</p>
          </div>

          <div v-if="products.length" class="product-list">
            <div v-for="p in products" :key="p.id" class="product-item" @click="goDetail(p.id)">
              <div class="p-img" v-if="p.imageUrl" :style="{ backgroundImage: `url(${p.imageUrl})` }" />
              <div class="p-img empty" v-else />
              <div class="p-info">
                <h4>{{ p.name }}</h4>
                <p v-if="p.description">{{ p.description.slice(0, 100) }}...</p>
              </div>
            </div>
          </div>

          <div v-if="selectedCat && !products.length" class="empty-state">
            <p>No products listed yet. Contact us for details.</p>
          </div>

          <!-- Subcategories inline -->
          <div v-if="selectedCat?.children?.length" class="subcats">
            <h3>Subcategories</h3>
            <div class="subcat-grid">
              <div v-for="sc in selectedCat.children" :key="sc.id" class="subcat-card" @click="selectSub(sc)">
                <div class="sc-img" v-if="sc.imageUrl" :style="{ backgroundImage: `url(${sc.imageUrl})` }" />
                <div class="sc-img empty" v-else />
                <div class="sc-info">
                  <h4>{{ sc.nameEn }}</h4>
                  <p v-if="sc.description">{{ sc.description }}</p>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
    <PublicFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'
import { api } from '../../api'

const route = useRoute()
const categories = ref<any[]>([])
const selectedCat = ref<any>(null)
const products = ref<any[]>([])

onMounted(async () => {
  categories.value = await api.categories.tree()
  const pid = route.query.cat
  if (pid) {
    const cat = categories.value.find((c: any) => c.id === Number(pid))
    if (cat) selectCategory(cat)
  }
})

async function selectCategory(cat: any) {
  selectedCat.value = cat
  products.value = await api.products.byCategory(cat.id)
}

function selectSub(sc: any) {
  // Inlined: show subcategory info directly
  selectedCat.value = { ...selectedCat.value, selectedSub: sc }
}

function goDetail(id: number) {
  window.location.href = `/product/${id}`
}
</script>

<style scoped>
.page-head { background: var(--yq-gray-900); color: #fff; padding: 48px 0; text-align: center; }
.page-head h1 { font-size: 36px; margin-bottom: 8px; }
.page-head p { opacity: 0.7; }
.section { padding-top: 48px; padding-bottom: 72px; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
.products-layout { display: grid; grid-template-columns: 260px 1fr; gap: 40px; }
.sidebar h3 { font-size: 14px; text-transform: uppercase; letter-spacing: 1px; color: #999; margin-bottom: 12px; }
.sidebar ul { list-style: none; }
.sidebar li {
  padding: 10px 12px; border-radius: 4px; cursor: pointer; display: flex; justify-content: space-between;
  font-size: 14px; color: var(--yq-gray-800); transition: all 0.15s;
}
.sidebar li:hover, .sidebar li.active { background: var(--yq-red); color: #fff; }
.cat-count { background: rgba(0,0,0,0.1); padding: 2px 8px; border-radius: 10px; font-size: 12px; }
.selected-header h2 { font-size: 22px; margin-bottom: 6px; }
.selected-header p { color: #666; font-size: 14px; }
.product-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; margin-top: 24px; }
.product-item { border: 1px solid var(--yq-gray-200); border-radius: 6px; overflow: hidden; cursor: pointer; transition: box-shadow 0.2s; }
.product-item:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.1); }
.p-img { height: 160px; background: var(--yq-gray-200); background-size: cover; background-position: center; }
.p-img.empty { background: repeating-linear-gradient(45deg, var(--yq-gray-100), var(--yq-gray-100) 10px, var(--yq-gray-200) 10px, var(--yq-gray-200) 20px); }
.p-info { padding: 12px; }
.p-info h4 { font-size: 14px; margin-bottom: 4px; }
.p-info p { font-size: 12px; color: #888; }
.subcats { margin-top: 40px; }
.subcats h3 { font-size: 18px; margin-bottom: 16px; }
.subcat-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
.subcat-card { border: 1px solid var(--yq-gray-200); border-radius: 6px; overflow: hidden; cursor: pointer; }
.sc-img { height: 120px; background: var(--yq-gray-200); background-size: cover; background-position: center; }
.sc-img.empty { background: repeating-linear-gradient(45deg, var(--yq-gray-100), var(--yq-gray-100) 10px, var(--yq-gray-200) 10px, var(--yq-gray-200) 20px); }
.sc-info { padding: 10px; }
.sc-info h4 { font-size: 13px; margin-bottom: 4px; }
.sc-info p { font-size: 11px; color: #888; }
.empty-state { padding: 40px; text-align: center; color: #999; }
@media (max-width: 768px) { .products-layout { grid-template-columns: 1fr; } }
</style>
