<template>
  <div>
    <PublicHeader />
    <div class="container section" v-if="product">
      <RouterLink to="/products" class="back">← Back to Products</RouterLink>
      <div class="detail-grid">
        <div class="detail-img" v-if="product.imageUrl" :style="{ backgroundImage: `url(${product.imageUrl})` }" />
        <div class="detail-img empty" v-else />
        <div class="detail-info">
          <h1>{{ product.name }}</h1>
          <p v-if="product.description" class="desc">{{ product.description }}</p>
          <div class="specs" v-if="specs.length">
            <h3>Specifications</h3>
            <table>
              <tr v-for="s in specs" :key="s.label"><td>{{ s.label }}</td><td>{{ s.value }}</td></tr>
            </table>
          </div>
          <RouterLink to="/about" class="btn-primary">Request a Quote</RouterLink>
        </div>
      </div>
    </div>
    <PublicFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'
import { api } from '../../api'

const route = useRoute()
const product = ref<any>(null)

const specs = computed(() => {
  if (!product.value?.description) return []
  const lines = product.value.description.split('\n').filter(Boolean)
  return lines.map((l: string) => {
    const [label, ...rest] = l.split(':')
    return { label: label.trim(), value: rest.join(':').trim() }
  })
})

onMounted(async () => {
  product.value = await api.products.get(Number(route.params.id))
})
</script>

<style scoped>
.section { padding: 32px 0 72px; max-width: 1000px; }
.back { display: inline-block; color: var(--yq-red); text-decoration: none; font-size: 14px; margin-bottom: 16px; }
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 40px; }
.detail-img { aspect-ratio: 4/3; background: var(--yq-gray-200); background-size: cover; background-position: center; border-radius: 6px; }
.detail-img.empty { background: repeating-linear-gradient(45deg, var(--yq-gray-100), var(--yq-gray-100) 10px, var(--yq-gray-200) 10px, var(--yq-gray-200) 20px); }
.detail-info h1 { font-size: 28px; margin-bottom: 12px; }
.desc { font-size: 14px; color: #555; line-height: 1.7; margin-bottom: 20px; }
.specs h3 { font-size: 16px; margin-bottom: 8px; }
.specs table { width: 100%; border-collapse: collapse; font-size: 13px; margin-bottom: 24px; }
.specs td { padding: 6px 0; border-bottom: 1px solid var(--yq-gray-200); }
.specs td:first-child { font-weight: 600; color: var(--yq-gray-800); width: 40%; }
.btn-primary { display: inline-block; background: var(--yq-red); color: #fff; padding: 12px 28px; border-radius: 4px; text-decoration: none; font-weight: 600; font-size: 14px; }
.btn-primary:hover { background: var(--yq-red-light); }
@media (max-width: 768px) { .detail-grid { grid-template-columns: 1fr; } }
</style>
