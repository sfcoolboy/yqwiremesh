<template>
  <div>
    <PublicHeader />
    <div class="container section" v-if="product">
      <RouterLink to="/products" class="back">← Back to Products</RouterLink>
      <div class="detail-grid">
        <div class="detail-img" v-if="product.imageUrl" :style="{ backgroundImage: `url(${product.imageUrl})` }"></div>
        <div class="detail-img empty" v-else></div>
        <div class="detail-info">
          <h1>{{ product.name }}</h1>
          <div class="title-divider" />
          <div class="specs" v-if="specs.length">
            <h3>Product Specifications</h3>
            <div class="spec-list">
              <div v-for="s in specs" :key="s.key" class="spec-row">
                <span class="spec-label">{{ s.label }}</span>
                <span class="spec-value">{{ s.value }}</span>
              </div>
            </div>
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
import { productSchema, setJsonLd } from '../../composables/useMeta'
import { api } from '../../api'

const route = useRoute()
const product = ref<any>(null)
const categoryParams = ref<any[]>([])

const specs = computed(() => {
  const raw = parseJson(product.value?.specs)
  const values: Record<string, { value: any; name?: string; unit?: string }> = {}
  const legacyItems: any[] = []

  if (Array.isArray(raw)) {
    raw.forEach((item: any) => {
      if (!item) return
      legacyItems.push(item)
      if (item.id !== undefined) values[String(item.id)] = item
      if (item.name) values[item.name] = item
    })
  } else if (raw && typeof raw === 'object') {
    Object.entries(raw).forEach(([key, value]) => {
      values[key] = { value }
    })
  }

  const rows = categoryParams.value
    .filter((item: any) => item && item.enabled !== false)
    .map((item: any) => {
      const match = values[String(item.id)] ?? values[item.name]
      return {
        key: String(item.id ?? item.name),
        label: item.name,
        value: formatSpecValue(match?.value, item.unit ?? match?.unit),
      }
    })
    .filter((item: any) => item.label && item.value)

  legacyItems.forEach((item: any, index: number) => {
    if (!item.name || !formatSpecValue(item.value, item.unit)) return
    if (rows.some((row: any) => row.label === item.name)) return
    rows.push({
      key: `legacy-${item.id ?? item.name ?? index}`,
      label: item.name,
      value: formatSpecValue(item.value, item.unit),
    })
  })

  if (!categoryParams.value.length) {
    Object.entries(values).forEach(([key, item]: [string, any]) => {
      if (!item.name && !item.value) return
      const label = item.name || key
      if (rows.some((row: any) => row.label === label)) return
      rows.push({
        key,
        label,
        value: formatSpecValue(item.value, item.unit),
      })
    })
  }

  return rows
})

function parseJson(value: any): any {
  if (!value) return null
  if (typeof value !== 'string') return value
  try {
    return JSON.parse(value)
  } catch {
    return null
  }
}

function formatSpecValue(value: any, unit?: string): string {
  if (value === undefined || value === null || value === '') return ''
  const text = String(value).trim()
  if (!unit || !text) return text
  return text.toLowerCase().includes(String(unit).toLowerCase())
    ? text
    : `${text} ${unit}`
}

onMounted(async () => {
  try {
    const [detail, categories] = await Promise.all([
      api.products.get(Number(route.params.id)),
      api.categories.listByType('PRODUCT'),
    ])
    product.value = detail
    const category = categories.find((item: any) => item.id === detail.categoryId)
    const definitions = parseJson(category?.specs)
    categoryParams.value = Array.isArray(definitions) ? definitions : []
    setJsonLd(productSchema(product.value, category))
  } catch {
    product.value = null
  }
})
</script>

<style scoped>
.section { padding: 32px 0 72px; max-width: 1000px; margin: 0 auto; }
.back { display: inline-block; color: var(--yq-red); text-decoration: none; font-size: 14px; margin-bottom: 16px; }
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 40px; margin: 0 auto; justify-content: center; }
.detail-img { aspect-ratio: 4/3; background: var(--yq-gray-200); background-size: cover; background-position: center; border-radius: 6px; }
.detail-img.empty { background: repeating-linear-gradient(45deg, var(--yq-gray-100), var(--yq-gray-100) 10px, var(--yq-gray-200) 10px, var(--yq-gray-200) 20px); }
.detail-info h1 { font-size: 28px; line-height: 1.3; margin-bottom: 14px; color: var(--yq-gray-900); }
.title-divider { width: 56px; height: 3px; background: var(--yq-red); margin-bottom: 24px; }
.specs h3 { font-size: 18px; margin-bottom: 12px; color: var(--yq-gray-900); }
.spec-list { border-top: 1px solid var(--yq-gray-200); margin-bottom: 28px; }
.spec-row { display: grid; grid-template-columns: minmax(130px, 0.9fr) minmax(0, 1.1fr); gap: 24px; padding: 12px 0; border-bottom: 1px solid var(--yq-gray-200); font-size: 14px; }
.spec-label { font-weight: 600; color: var(--yq-gray-800); }
.spec-value { color: #555; text-align: right; overflow-wrap: anywhere; }
.btn-primary { display: inline-block; background: var(--yq-red); color: #fff; padding: 12px 28px; border-radius: 4px; text-decoration: none; font-weight: 600; font-size: 14px; }
.btn-primary:hover { background: var(--yq-red-light); }
@media (max-width: 768px) {
  .detail-grid { grid-template-columns: 1fr; gap: 28px; }
  .spec-row { grid-template-columns: 1fr; gap: 4px; }
  .spec-value { text-align: left; }
}
</style>




