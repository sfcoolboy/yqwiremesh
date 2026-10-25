<template>
  <div>
    <PublicHeader />
    <div class="page-head">
      <h1>Our Factory</h1>
      <p>See where quality metal products are made — Anping, Hebei, China.</p>
    </div>
    <div class="container section">
      <div class="gallery">
        <div v-for="(photo, i) in photos" :key="i" class="gallery-item" @click="openLightbox(i)">
          <img :src="photo.url" :alt="photo.caption" loading="lazy" />
          <div class="gallery-caption">{{ photo.caption }}</div>
        </div>
      </div>
      <div v-if="lightboxIdx !== null" class="lightbox" @click="lightboxIdx = null">
        <img :src="photos[lightboxIdx].url" :alt="photos[lightboxIdx].caption" />
        <span class="lightbox-close">&times;</span>
        <button class="lb-nav" @click.stop="prev()">‹</button>
        <button class="lb-nav right" @click.stop="next()">›</button>
      </div>
    </div>
    <PublicFooter />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'

// Placeholder photos — replace with real factory images when available
const photos = ref([
  { url: '', caption: 'Production Workshop' },
  { url: '', caption: 'Hot-dip Galvanizing Line' },
  { url: '', caption: 'Welding Production Line' },
  { url: '', caption: 'Quality Control Lab' },
  { url: '', caption: 'Finished Goods Warehouse' },
  { url: '', caption: 'Loading Dock' },
])
const lightboxIdx = ref<number | null>(null)
const prev = () => { if (lightboxIdx.value) lightboxIdx.value = (lightboxIdx.value - 1 + photos.value.length) % photos.value.length }
const next = () => { if (lightboxIdx.value !== null) lightboxIdx.value = (lightboxIdx.value + 1) % photos.value.length }
const openLightbox = (i: number) => { lightboxIdx.value = i }
</script>

<style scoped>
.page-head { background: var(--yq-gray-900); color: #fff; padding: 48px 0; text-align: center; }
.page-head h1 { font-size: 36px; margin-bottom: 8px; }
.page-head p { opacity: 0.7; }
.section { padding: 48px 0 72px; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
.gallery { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.gallery-item { cursor: pointer; border-radius: 6px; overflow: hidden; position: relative; aspect-ratio: 4/3; background: var(--yq-gray-200); }
.gallery-item img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s; }
.gallery-item:hover img { transform: scale(1.05); }
.gallery-caption { position: absolute; bottom: 0; left: 0; right: 0; padding: 8px 12px; background: linear-gradient(transparent, rgba(0,0,0,0.7)); color: #fff; font-size: 13px; }
.lightbox { position: fixed; inset: 0; background: rgba(0,0,0,0.9); z-index: 200; display: flex; align-items: center; justify-content: center; }
.lightbox img { max-width: 90vw; max-height: 85vh; border-radius: 4px; }
.lightbox-close { position: absolute; top: 20px; right: 28px; color: #fff; font-size: 36px; cursor: pointer; }
.lb-nav { position: absolute; top: 50%; transform: translateY(-50%); background: rgba(255,255,255,0.15); border: none; color: #fff; font-size: 32px; width: 48px; height: 48px; cursor: pointer; border-radius: 4px; }
.lb-nav.right { right: 20px; }
.lb-nav:not(.right) { left: 20px; }
@media (max-width: 768px) { .gallery { grid-template-columns: 1fr; } }
</style>
