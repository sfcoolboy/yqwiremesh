<template>
  <div>
    <PublicHeader />

    <!-- Hero -->
    <section class="hero">
      <div class="hero-bg" />
      <div class="hero-content">
        <h1>Professional Metal Grating &amp; Wire Mesh Solutions</h1>
        <p>Trusted by builders and contractors across Southeast Asia and Europe since 2005.</p>
        <div class="hero-actions">
          <RouterLink to="/products" class="btn-primary">Explore Products</RouterLink>
          <RouterLink to="/about" class="btn-outline">Contact Us</RouterLink>
        </div>
      </div>
    </section>

    <!-- Products preview -->
    <section class="section">
      <div class="container">
        <h2 class="section-title">Our Products</h2>
        <div class="product-grid">
          <div v-for="cat in categories" :key="cat.id" class="product-card" @click="goProduct(cat.id)">
            <div class="product-img" :style="{ backgroundImage: cat.imageUrl ? `url(${cat.imageUrl})` : 'none' }" />
            <div class="product-info">
              <h3>{{ cat.nameEn }}</h3>
              <p v-if="cat.description">{{ cat.description }}</p>
            </div>
          </div>
        </div>
        <div class="center-link"><RouterLink to="/products">View All Products →</RouterLink></div>
      </div>
    </section>

    <!-- Factory preview -->
    <section class="section factory-preview">
      <div class="container">
        <div class="factory-grid">
          <div class="factory-text">
            <h2>Our Factory</h2>
            <p>Anping County, Hebei — the Hometown of Wire Mesh in China. Over 20,000 sqm of production space with CNC cutting, automated welding, and hot-dip galvanizing lines.</p>
            <RouterLink to="/factory" class="btn-outline">See Factory Photos →</RouterLink>
          </div>
          <div class="factory-stats">
            <div class="stat"><strong>18+</strong><span>Years</span></div>
            <div class="stat"><strong>50+</strong><span>Countries</span></div>
            <div class="stat"><strong>20K+</strong><span>Sqm Facility</span></div>
            <div class="stat"><strong>ISO</strong><span>Certified</span></div>
          </div>
        </div>
      </div>
    </section>

    <!-- Latest news -->
    <section class="section">
      <div class="container">
        <h2 class="section-title">Latest News</h2>
        <div class="news-grid">
          <div v-for="n in recentNews" :key="n.id" class="news-card" @click="goNews(n.id)">
            <div class="news-thumb" v-if="n.coverImageUrl" :style="{ backgroundImage: `url(${n.coverImageUrl})` }" />
            <div class="news-body">
              <span class="news-tag">{{ n.category }}</span>
              <h4>{{ n.title }}</h4>
              <p>{{ n.summary }}</p>
              <span class="news-date">{{ formatDate(n.publishDate) }}</span>
            </div>
          </div>
        </div>
        <div class="center-link"><RouterLink to="/news">All News →</RouterLink></div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta-section">
      <div class="container">
        <h2>Ready to Work Together?</h2>
        <p>Send us your inquiry and our team will respond within 24 hours.</p>
        <RouterLink to="/about" class="btn-white">Get a Quote</RouterLink>
      </div>
    </section>

    <PublicFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'
import { api } from '../../api'

const categories = ref<any[]>([])
const recentNews = ref<any[]>([])

onMounted(async () => {
  categories.value = await api.categories.tree()
  recentNews.value = (await api.news.list()).slice(0, 3)
})

const goProduct = (id: number) => { window.location.href = `/products?cat=${id}` }
const goNews = (id: number) => { window.location.href = `/news/${id}` }
const formatDate = (d: string) => new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
</script>

<style scoped>
.hero { position: relative; height: 520px; display: flex; align-items: center; overflow: hidden; }
.hero-bg { position: absolute; inset: 0; background: var(--yq-gray-900); }
.hero-bg::after { content: ''; position: absolute; inset: 0; background: linear-gradient(135deg, rgba(139,26,26,0.85), rgba(26,26,26,0.9)); }
.hero-content { position: relative; z-index: 1; max-width: 1200px; margin: 0 auto; padding: 0 24px; color: #fff; }
.hero h1 { font-size: clamp(28px, 5vw, 52px); font-weight: 800; line-height: 1.15; margin-bottom: 16px; }
.hero p { font-size: 18px; opacity: 0.85; margin-bottom: 28px; max-width: 560px; }
.hero-actions { display: flex; gap: 12px; flex-wrap: wrap; }
.btn-primary, .btn-outline, .btn-white {
  display: inline-block; padding: 12px 28px; border-radius: 4px; font-size: 14px; font-weight: 600;
  text-decoration: none; transition: all 0.15s; cursor: pointer; border: none;
}
.btn-primary { background: var(--yq-red); color: #fff; }
.btn-primary:hover { background: var(--yq-red-light); }
.btn-outline { background: transparent; color: #fff; border: 1.5px solid rgba(255,255,255,0.5); }
.btn-outline:hover { border-color: #fff; }
.btn-white { background: #fff; color: var(--yq-red); }
.btn-white:hover { background: var(--yq-gray-100); }
.section { padding: 72px 0; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
.section-title { font-size: 28px; font-weight: 700; margin-bottom: 36px; color: var(--yq-gray-900); }
.product-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.product-card { border: 1px solid var(--yq-gray-200); border-radius: 6px; overflow: hidden; cursor: pointer; transition: box-shadow 0.2s; }
.product-card:hover { box-shadow: 0 8px 24px rgba(0,0,0,0.1); }
.product-img { height: 200px; background: var(--yq-gray-200); background-size: cover; background-position: center; }
.product-info { padding: 16px; }
.product-info h3 { font-size: 16px; margin-bottom: 6px; }
.product-info p { font-size: 13px; color: #666; }
.center-link { text-align: center; margin-top: 32px; }
.center-link a { color: var(--yq-red); text-decoration: none; font-weight: 600; font-size: 14px; }
.factory-preview { background: var(--yq-gray-50); }
.factory-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 48px; align-items: center; }
.factory-text h2 { font-size: 28px; margin-bottom: 16px; }
.factory-text p { color: #555; line-height: 1.7; margin-bottom: 20px; }
.factory-text .btn-outline { color: var(--yq-red); border-color: var(--yq-red); }
.factory-text .btn-outline:hover { background: var(--yq-red); color: #fff; }
.factory-stats { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.stat { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 20px; text-align: center; }
.stat strong { display: block; font-size: 28px; color: var(--yq-red); }
.stat span { font-size: 12px; color: #888; text-transform: uppercase; letter-spacing: 0.5px; }
.news-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.news-card { display: flex; border: 1px solid var(--yq-gray-200); border-radius: 6px; overflow: hidden; cursor: pointer; transition: box-shadow 0.2s; }
.news-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.08); }
.news-thumb { width: 140px; min-height: 120px; background: var(--yq-gray-200); background-size: cover; background-position: center; flex-shrink: 0; }
.news-body { padding: 14px; flex: 1; }
.news-tag { font-size: 11px; text-transform: uppercase; color: var(--yq-red); font-weight: 600; }
.news-body h4 { font-size: 14px; margin: 6px 0; line-height: 1.3; }
.news-body p { font-size: 12px; color: #666; margin: 4px 0; }
.news-date { font-size: 11px; color: #999; }
.cta-section { background: var(--yq-red); color: #fff; padding: 64px 0; text-align: center; }
.cta-section h2 { font-size: 28px; margin-bottom: 12px; }
.cta-section p { margin-bottom: 24px; opacity: 0.9; }
@media (max-width: 768px) {
  .product-grid, .news-grid { grid-template-columns: 1fr; }
  .factory-grid { grid-template-columns: 1fr; }
  .hero { height: auto; padding: 80px 0; }
}
</style>
