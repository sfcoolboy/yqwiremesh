<template>
  <div>
    <PublicHeader />
    <div class="page-head">
      <h1>News & Insights</h1>
      <p>Company updates, industry trends, and exhibition highlights.</p>
    </div>
    <div class="container section">
      <div class="filter-bar">
        <button v-for="c in cats" :key="c" :class="{ active: filter === c }" @click="filter = c">{{ cLabel(c) }}</button>
      </div>
      <div class="news-list">
        <div v-for="n in filtered" :key="n.id" class="news-row" @click="goDetail(n.id)">
          <div class="news-thumb" v-if="n.coverImageUrl" :style="{ backgroundImage: `url(${n.coverImageUrl})` }" />
          <div class="news-thumb empty" v-else />
          <div class="news-content">
            <span class="news-cat">{{ n.category }}</span>
            <h3>{{ n.title }}</h3>
            <p>{{ n.summary }}</p>
            <span class="date">{{ formatDate(n.publishDate) }}</span>
          </div>
        </div>
      </div>
    </div>
    <PublicFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'
import { api } from '../../api'

const allNews = ref<any[]>([])
const filter = ref('All')
const cats = ['All', 'Company News', 'Industry News', 'Exhibition']

const filtered = computed(() =>
  filter.value === 'All' ? allNews.value : allNews.value.filter((n: any) => n.category === filter.value)
)

const cLabel = (c: string) => c

function goDetail(id: number) { window.location.href = `/news/${id}` }
function formatDate(d: string) { return new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' }) }

onMounted(async () => { allNews.value = await api.news.list() })
</script>

<style scoped>
.page-head { background: var(--yq-gray-900); color: #fff; padding: 48px 0; text-align: center; }
.page-head h1 { font-size: 36px; margin-bottom: 8px; }
.page-head p { opacity: 0.7; }
.section { padding: 48px 0 72px; }
.container { max-width: 900px; margin: 0 auto; padding: 0 24px; }
.filter-bar { display: flex; gap: 8px; margin-bottom: 28px; flex-wrap: wrap; }
.filter-bar button {
  padding: 6px 16px; border: 1px solid var(--yq-gray-200); border-radius: 20px; background: #fff;
  font-size: 13px; cursor: pointer; transition: all 0.15s;
}
.filter-bar button.active { background: var(--yq-red); color: #fff; border-color: var(--yq-red); }
.news-row { display: flex; gap: 16px; padding: 16px; border: 1px solid var(--yq-gray-200); border-radius: 6px; margin-bottom: 12px; cursor: pointer; transition: box-shadow 0.2s; }
.news-row:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.08); }
.news-thumb { width: 180px; height: 110px; flex-shrink: 0; background: var(--yq-gray-200); background-size: cover; background-position: center; border-radius: 4px; }
.news-thumb.empty { background: repeating-linear-gradient(45deg, var(--yq-gray-100), var(--yq-gray-100) 10px, var(--yq-gray-200) 10px, var(--yq-gray-200) 20px); }
.news-content { flex: 1; }
.news-cat { font-size: 11px; text-transform: uppercase; color: var(--yq-red); font-weight: 600; }
.news-content h3 { font-size: 16px; margin: 6px 0; }
.news-content p { font-size: 13px; color: #666; line-height: 1.5; }
.date { font-size: 12px; color: #999; }
@media (max-width: 600px) { .news-row { flex-direction: column; } .news-thumb { width: 100%; height: 180px; } }
</style>
