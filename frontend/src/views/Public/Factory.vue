<template>
  <div>
    <PublicHeader />
    <section class="page-hero">
      <div class="hero-bg" :style="heroImage ? { backgroundImage: `url(${heroImage})` } : {}" />
      <div class="yq-container">
        <span class="yq-kicker">Inside Our Facility</span>
        <h1>Our Factory</h1>
        <p>See where quality metal products are made — Anping, Hebei, China.</p>
      </div>
    </section>
    <div class="yq-container page-section">
      <div v-if="loading" class="loading-state">
        <div class="skeleton hero-skel" />
      </div>
      <div v-else-if="photos.length" class="gallery">
        <div v-for="(photo, i) in photos" :key="i" class="gallery-item" @click="openLightbox(i)">
          <img :src="photo.imageUrl" :alt="photo.caption" loading="lazy" />
          <div class="gallery-caption">{{ photo.caption }}</div>
        </div>
      </div>
      <div v-else class="empty-gallery">Factory photos are being updated. Please check back soon.</div>
      <div v-if="lightboxIdx !== null" class="lightbox" @click="lightboxIdx = null">
        <img :src="photos[lightboxIdx].imageUrl" :alt="photos[lightboxIdx].caption" />
        <span class="lightbox-close" aria-label="Close">&times;</span>
        <button class="lb-nav" @click.stop="prev()" aria-label="Previous">&#8249;</button>
        <button class="lb-nav right" @click.stop="next()" aria-label="Next">&#8250;</button>
      </div>
    </div>
    <PublicFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import PublicHeader from "../../components/PublicHeader.vue";
import PublicFooter from "../../components/PublicFooter.vue";
import { api } from "../../api";

const photos = ref<any[]>([]);
const lightboxIdx = ref<number | null>(null);
const loading = ref(true);
const heroImage = ref("");

const prev = () => { if (lightboxIdx.value) lightboxIdx.value = (lightboxIdx.value - 1 + photos.value.length) % photos.value.length };
const next = () => { if (lightboxIdx.value !== null) lightboxIdx.value = (lightboxIdx.value + 1) % photos.value.length };
const openLightbox = (i: number) => { lightboxIdx.value = i };

function onClose(e: KeyboardEvent) {
  if (e.key === "Escape") lightboxIdx.value = null;
}

onMounted(async () => {
  try {
    const list = await api.factoryImages.forFactory();
    photos.value = list.filter((p: any) => p.imageUrl).map((p: any) => ({ imageUrl: p.imageUrl, caption: p.caption || "" }));
  } catch {
    photos.value = [];
  } finally {
    try { const h = await api.pageHeroes.getByPage("factory"); heroImage.value = h?.imageUrl || ""; } catch {}
  loading.value = false;
  }
});

onMounted(() => {
  document.addEventListener("keydown", onClose);
});

onUnmounted(() => document.removeEventListener("keydown", onClose));
</script>

<style scoped>
.loading-state { padding: 64px 0; display: flex; justify-content: center; }
.skeleton { width: 80%; max-width: 800px; height: 400px; background: linear-gradient(90deg, var(--yq-gray-100) 25%, var(--yq-gray-200) 50%, var(--yq-gray-100) 75%); background-size: 200% 100%; animation: shimmer 1.5s infinite; border-radius: 8px; }
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }

.gallery { display: grid; grid-template-columns: repeat(2, minmax(0, 560px)); justify-content: center; gap: 40px; }
.gallery-item { width: 100%; max-width: 560px; aspect-ratio: 4 / 3; cursor: pointer; overflow: hidden; position: relative; background: var(--yq-steel-800); }
.gallery-item img { display: block; width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s; }
.gallery-item:hover img { transform: scale(1.05); }
.gallery-caption { position: absolute; bottom: 0; left: 0; right: 0; padding: 10px 14px; background: linear-gradient(transparent, rgba(14,16,18,0.85)); color: #fff; font-size: 13px; }
.empty-gallery { text-align: center; color: #888; font-size: 15px; padding: 64px 24px; background: #fff; border: 1px dashed var(--yq-gray-200); }
.lightbox { position: fixed; inset: 0; background: rgba(0,0,0,0.9); z-index: 200; display: flex; align-items: center; justify-content: center; }
.lightbox img { max-width: 90vw; max-height: 85vh; border-radius: 4px; }
.lightbox-close { position: absolute; top: 20px; right: 28px; color: #fff; font-size: 36px; cursor: pointer; }
.lb-nav { position: absolute; top: 50%; transform: translateY(-50%); background: rgba(255,255,255,0.15); border: none; color: #fff; font-size: 32px; width: 48px; height: 48px; cursor: pointer; border-radius: 4px; }
.lb-nav.right { right: 20px; }
.lb-nav:not(.right) { left: 20px; }
@media (max-width: 1040px) {
  .gallery { grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 20px; }
  .gallery-item { justify-self: center; }
}
@media (max-width: 768px) {
  .gallery { grid-template-columns: minmax(0, 1fr); justify-content: center; }
}
</style>
