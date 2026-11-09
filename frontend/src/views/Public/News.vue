<template>
  <div>
    <PublicHeader />
    <section class="page-hero">
      <div class="hero-bg" :style="heroImage ? { backgroundImage: `url(${heroImage})` } : {}" />
      <div class="yq-container">
        <span class="yq-kicker">Latest Information</span>
        <h1>News &amp; Insights</h1>
        <p>Company updates, industry trends, and exhibition highlights.</p>
      </div>
    </section>
    <div class="yq-container page-section">
      <div v-if="loading" class="loading-state">
        <div class="skeleton line-skel" /><div class="skeleton line-skel short" />
      </div>
      <div v-else>
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
        <div v-if="!filtered.length" class="empty-state">No news articles found.</div>
      </div>
    </div>
    <PublicFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import PublicHeader from "../../components/PublicHeader.vue";
import PublicFooter from "../../components/PublicFooter.vue";
import { api } from "../../api";

const router = useRouter();
const allNews = ref<any[]>([]);
const newsCategories = ref<any[]>([]);
const filter = ref("All");
const loading = ref(true);
const heroImage = ref("");

const cats = computed(() => ["All", ...newsCategories.value.map((c: any) => c.nameEn)]);
const filtered = computed(() =>
  filter.value === "All" ? allNews.value : allNews.value.filter((n: any) => n.category === filter.value)
);

const cLabel = (c: string) => c;

function goDetail(id: number) {
  router.push(`/news/${id}`);
}

function formatDate(d: string) {
  return new Date(d).toLocaleDateString("en-US", { year: "numeric", month: "long", day: "numeric" });
}

onMounted(async () => {
  try {
    allNews.value = await api.news.list();
  } catch {
    allNews.value = [];
  }
  try {
    newsCategories.value = await api.categories.listByType("NEWS");
  } catch {
    newsCategories.value = [];
  }
  try { const h = await api.pageHeroes.getByPage("news"); heroImage.value = h?.imageUrl || ""; } catch {}
  loading.value = false;
});
</script>

<style scoped>
.loading-state { padding: 64px 0; display: flex; flex-direction: column; gap: 12px; align-items: center; }
.skeleton { background: linear-gradient(90deg, var(--yq-gray-100) 25%, var(--yq-gray-200) 50%, var(--yq-gray-100) 75%); background-size: 200% 100%; animation: shimmer 1.5s infinite; border-radius: 4px; }
.line-skel { width: 600px; max-width: 80%; height: 20px; }
.line-skel.short { width: 400px; }
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }

.filter-bar { display: flex; gap: 8px; margin-bottom: 28px; flex-wrap: wrap; }
.filter-bar button {
  padding: 7px 18px; border: 1px solid var(--yq-gray-200); border-radius: 2px; background: #fff;
  font-size: 13px; cursor: pointer; transition: all 0.15s;
}
.filter-bar button.active { background: var(--yq-red); color: #fff; border-color: var(--yq-red); }
.news-row { display: flex; gap: 18px; padding: 16px; border: 1px solid var(--yq-gray-200); margin-bottom: 12px; cursor: pointer; transition: border-color 0.2s, transform 0.2s; background: #fff; }
.news-row:hover { border-color: var(--yq-red); transform: translateY(-2px); }
.news-thumb { width: 200px; height: 120px; flex-shrink: 0; background: var(--yq-gray-200); background-size: cover; background-position: center; }
.news-thumb.empty { background: repeating-linear-gradient(45deg, var(--yq-gray-100), var(--yq-gray-100) 10px, var(--yq-gray-200) 10px, var(--yq-gray-200) 20px); }
.news-content { flex: 1; }
.news-cat { font-size: 11px; text-transform: uppercase; color: var(--yq-red); font-weight: 600; }
.news-content h3 { font-size: 16px; margin: 6px 0; }
.news-content p { font-size: 13px; color: #666; line-height: 1.5; }
.date { font-size: 12px; color: #999; }
.empty-state { text-align: center; color: #999; padding: 48px; }
@media (max-width: 600px) { .news-row { flex-direction: column; } .news-thumb { width: 100%; height: 180px; } }
</style>