<template>
  <div>
    <PublicHeader />
    <div class="container section" v-if="news">
      <RouterLink to="/news" class="back">← Back to News</RouterLink>
      <article class="article">
        <h1>{{ news.title }}</h1>
        <div class="meta">
          <span class="cat">{{ news.category }}</span>
          <span>{{ formatDate(news.publishDate) }}</span>
        </div>
        <div class="cover" v-if="news.coverImageUrl" :style="{ backgroundImage: `url(${news.coverImageUrl})` }" />
        <div class="body" v-html="news.content" />
      </article>
    </div>
    <PublicFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'
import { api } from '../../api'

const route = useRoute()
const news = ref<any>(null)

onMounted(async () => {
  news.value = await api.news.get(Number(route.params.id))
})

function formatDate(d: string) { return new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' }) }
</script>

<style scoped>
.section { padding: 32px 0 72px; max-width: 800px; }
.back { display: inline-block; color: var(--yq-red); text-decoration: none; font-size: 14px; margin-bottom: 16px; }
.article h1 { font-size: 28px; margin-bottom: 8px; line-height: 1.3; }
.meta { display: flex; gap: 16px; font-size: 13px; color: #888; margin-bottom: 24px; }
.cat { color: var(--yq-red); font-weight: 600; }
.cover { height: 320px; background: var(--yq-gray-200); background-size: cover; background-position: center; border-radius: 6px; margin-bottom: 24px; }
.body { font-size: 15px; line-height: 1.8; color: #333; }
.body :deep(h2) { font-size: 20px; margin: 24px 0 12px; }
.body :deep(img) { max-width: 100%; border-radius: 4px; }
</style>
