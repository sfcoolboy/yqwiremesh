<template>
  <div>
    <PublicHeader />
    <section class="page-hero">
      <div class="yq-container">
        <span class="yq-kicker">Product Range</span>
        <h1>Products</h1>
        <p>Explore our full range of metal grating and wire mesh solutions.</p>
      </div>
    </section>
    <div class="yq-container page-section">
      <div v-if="loading" class="loading-state">
        <div class="skeleton line-skel" /><div class="skeleton line-skel short" />
      </div>
      <div v-else class="products-layout">
        <aside class="sidebar">
          <h3>Categories</h3>
          <ul>
            <li v-for="cat in categories" :key="cat.id" :class="{ active: selectedCat?.id === cat.id }" @click="selectCategory(cat)">
              <span class="cat-name">{{ cat.nameEn }}</span>
              <span class="cat-count">{{ cat.productCount ?? 0 }}</span>
            </li>
          </ul>
        </aside>
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
              </div>
            </div>
          </div>
          <div v-if="selectedCat && !products.length" class="empty-state">
            <p>No products listed yet. Contact us for details.</p>
          </div>
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
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import PublicHeader from "../../components/PublicHeader.vue";
import PublicFooter from "../../components/PublicFooter.vue";
import { api } from "../../api";

const route = useRoute();
const router = useRouter();
const categories = ref<any[]>([]);
const selectedCat = ref<any>(null);
const products = ref<any[]>([]);
const heroImage = ref("");
const loading = ref(true);

async function loadProductCounts(allProducts: any[]) {
  const counts: Record<number, number> = {};
  for (const p of allProducts) {
    if (p.categoryId) counts[p.categoryId] = (counts[p.categoryId] || 0) + 1;
  }
  for (const cat of categories.value) cat.productCount = counts[cat.id] ?? 0;
}

async function autoSelectFirstAvailable() {
  for (const cat of categories.value) {
    if (cat.productCount > 0) { await selectCategory(cat); return; }
  }
}

onMounted(async () => {
  try {
    categories.value = await api.categories.tree();
  } catch {
    categories.value = [];
  }
  try {
    const allProducts = await api.products.list();
    await loadProductCounts(allProducts);
  } catch { /* ignore */ }
  const pid = route.query.cat;
  if (pid) {
    const cat = categories.value.find((c: any) => c.id === Number(pid));
    if (cat) {
      await selectCategory(cat);
      if (!products.value.length) await autoSelectFirstAvailable();
    } else {
      await autoSelectFirstAvailable();
    }
  } else {
    await autoSelectFirstAvailable();
  }
  loading.value = false;
});

async function selectCategory(cat: any) {
  selectedCat.value = cat;
  loading.value = true;
  try {
    products.value = await api.products.byCategory(cat.id);
  } catch {
    products.value = [];
  } finally {
    loading.value = false;
  }
}

function selectSub(sc: any) {
  selectedCat.value = { ...selectedCat.value, selectedSub: sc };
}

function goDetail(id: number) {
  router.push(`/product/${id}`);
}
</script>

<style scoped>
.loading-state { padding: 64px 0; display: flex; flex-direction: column; gap: 12px; align-items: center; }
.skeleton { background: linear-gradient(90deg, var(--yq-gray-100) 25%, var(--yq-gray-200) 50%, var(--yq-gray-100) 75%); background-size: 200% 100%; animation: shimmer 1.5s infinite; border-radius: 4px; }
.line-skel { width: 600px; max-width: 80%; height: 20px; }
.line-skel.short { width: 400px; }
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }

.products-layout { display: grid; grid-template-columns: 260px 1fr; gap: 48px; }
.sidebar h3 { font-size: 11px; text-transform: uppercase; letter-spacing: 0.12em; color: var(--yq-gray-400); margin-bottom: 14px; }
.sidebar ul { list-style: none; border-top: 1px solid var(--yq-gray-200); }
.sidebar li { padding: 14px 4px; border-bottom: 1px solid var(--yq-gray-200); cursor: pointer; display: flex; justify-content: space-between; align-items: center; font-size: 14px; color: var(--yq-gray-800); transition: color 0.15s; }
.sidebar li:hover, .sidebar li.active { color: var(--yq-red); }
.sidebar li.active .cat-name { font-weight: 700; }
.cat-count { font-size: 11px; color: var(--yq-gray-400); }
.selected-header h2 { font-size: 22px; margin-bottom: 6px; }
.selected-header p { color: #666; font-size: 14px; }
.product-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; margin-top: 24px; }
.product-item { border: 1px solid var(--yq-gray-200); overflow: hidden; cursor: pointer; transition: border-color 0.2s, transform 0.2s; }
.product-item:hover { border-color: var(--yq-red); transform: translateY(-3px); }
.p-img { height: 180px; background: var(--yq-gray-200); background-size: cover; background-position: center; }
.p-img.empty { background: repeating-linear-gradient(45deg, var(--yq-gray-100), var(--yq-gray-100) 10px, var(--yq-gray-200) 10px, var(--yq-gray-200) 20px); }
.p-info { padding: 16px; border-top: 3px solid var(--yq-red); }
.p-info h4 { font-size: 18px; line-height: 1.35; margin: 0; color: var(--yq-gray-900); }
.subcats { margin-top: 40px; }
.subcats h3 { font-size: 18px; margin-bottom: 16px; }
.subcat-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
.subcat-card { border: 1px solid var(--yq-gray-200); overflow: hidden; cursor: pointer; transition: border-color 0.2s; }
.subcat-card:hover { border-color: var(--yq-red); }
.sc-img { height: 120px; background: var(--yq-gray-200); background-size: cover; background-position: center; }
.sc-img.empty { background: repeating-linear-gradient(45deg, var(--yq-gray-100), var(--yq-gray-100) 10px, var(--yq-gray-200) 10px, var(--yq-gray-200) 20px); }
.sc-info { padding: 10px; }
.sc-info h4 { font-size: 13px; margin-bottom: 4px; }
.sc-info p { font-size: 11px; color: #888; }
.empty-state { padding: 40px; text-align: center; color: #999; }
@media (max-width: 768px) { .products-layout { grid-template-columns: 1fr; } }
</style>
