<template>
  <div>
    <PublicHeader />

    <!-- Hero -->
    <section class="hero">
      <div class="hero-bg" :style="heroImage ? { backgroundImage: `url(${heroImage})` } : {}"></div>
      <div class="hero-overlay"></div>
      <div class="hero-grid yq-grid-overlay"></div>
      <div class="hero-inner yq-container">
        <span class="yq-kicker hero-kicker">EST. 2005 · ANPING, CHINA</span>
        <h1>{{ heroTitle }}</h1>
        <p>{{ heroSubtitle }}</p>
        <div class="hero-actions">
          <RouterLink to="/products" class="yq-btn yq-btn-primary">Explore Products</RouterLink>
          <RouterLink to="/about" class="yq-btn yq-btn-ghost">Request a Quote</RouterLink>
        </div>
      </div>

    </section>

    <!-- Loading state -->
    <div v-if="loading" class="loading-state">
      <div class="skeleton hero-skel"></div>
      <div class="skeleton line-skel"></div>
      <div class="skeleton line-skel short"></div>
    </div>

    <!-- Why choose us -->
    <section v-if="!loading" class="why-section">
      <div class="yq-container">
        <div class="why-head">
          <div class="why-head-inner">
            <span class="why-badge">Why Choose YQ Metals</span>
            <h2 class="why-title">{{ s("home.why.title", "Your trusted partner for industrial metal products") }}</h2>
          </div>
          <p class="why-lead">
            Steel grating and galvanized wire manufactured in Anping, China, with full
            traceability from raw material to shipment.
          </p>
        </div>
        <div class="why-grid">
          <div v-for="(a, i) in advantages" :key="i" class="why-card">
            <div class="why-top">
              <div class="why-num-wrap">
                <div class="why-num"><span class="underscore">_</span>0{{ i + 1 }}</div>
              </div>
              <div class="why-icon-wrap">
                <svg v-if="i===0" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg" class="why-icon">
                  <rect x="3" y="20" width="15" height="24" rx="1" stroke="currentColor" stroke-width="2.4" stroke-linejoin="round"/>
                  <rect x="21" y="10" width="23" height="34" rx="1" stroke="currentColor" stroke-width="2.4" stroke-linejoin="round"/>
                  <rect x="25" y="16" width="6" height="6" rx="0.5" stroke="currentColor" stroke-width="2"/>
                  <rect x="35" y="16" width="6" height="6" rx="0.5" stroke="currentColor" stroke-width="2"/>
                  <rect x="25" y="26" width="6" height="6" rx="0.5" stroke="currentColor" stroke-width="2"/>
                  <rect x="35" y="26" width="6" height="6" rx="0.5" stroke="currentColor" stroke-width="2"/>
                  <path d="M10.5 20V8l6-5 6 5v12" stroke="currentColor" stroke-width="2.4" stroke-linejoin="round"/>
                  <line x1="3" y1="44" x2="44" y2="44" stroke="currentColor" stroke-width="2.5"/>
                </svg>
                <svg v-else-if="i===1" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg" class="why-icon">
                  <path d="M24 3L8 12.5v11.5c0 10 7.2 17 16 20 8.8-3 16-10 16-20V12.5L24 3z" stroke="currentColor" stroke-width="2.4" stroke-linejoin="round"/>
                  <path d="M15 24l5 5 12-14" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <svg v-else-if="i===2" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg" class="why-icon">
                  <circle cx="24" cy="24" r="19" stroke="currentColor" stroke-width="2.4"/>
                  <circle cx="24" cy="24" r="13" stroke="currentColor" stroke-width="1.4" opacity="0.4"/>
                  <path d="M24 12v14l10 6" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="24" cy="24" r="2.5" fill="currentColor"/>
                  <line x1="24" y1="5" x2="24" y2="9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <line x1="24" y1="39" x2="24" y2="43" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <line x1="5" y1="24" x2="9" y2="24" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <line x1="39" y1="24" x2="43" y2="24" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <path d="M36 10l2-2M10 10l-2-2M36 38l2 2M10 38l-2 2" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
                </svg>
                <svg v-else viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg" class="why-icon">
                  <circle cx="14" cy="28" r="9" stroke="currentColor" stroke-width="2.4"/>
                  <circle cx="14" cy="28" r="3" stroke="currentColor" stroke-width="1.8" opacity="0.5"/>
                  <circle cx="35" cy="28" r="9" stroke="currentColor" stroke-width="2.4"/>
                  <circle cx="35" cy="28" r="3" stroke="currentColor" stroke-width="1.8" opacity="0.5"/>
                  <path d="M14 19v-4m0 26v-4m22-13h5m-5 23h5" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"/>
                  <path d="M18.5 22l7-3.5 7 3.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <line x1="25.5" y1="18.5" x2="25.5" y2="29" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </div>
            </div>
            <div class="why-main">
              <h3>{{ a.title }}</h3>
            </div>
            <div class="why-line" />
              <div class="why-line"></div>
            <p>{{ a.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- About -->
    <section v-if="!loading" class="about-section">
      <div class="yq-container about-grid">
        <div class="about-copy">
          <span class="yq-kicker">About YQ Metals</span>
          <h2>{{ s("home.about.title", "Manufacturing metal solutions since 2005") }}</h2>
          <p>{{ s("home.about.p1", "Based in Anping County, Hebei — the hometown of wire mesh in China — we produce steel grating and galvanized steel wire for construction and industrial projects worldwide.") }}</p>
          <p>{{ s("home.about.p2", "Our 20,000 m² facility combines CNC cutting, automated welding and hot-dip galvanizing with in-house quality control on every batch.") }}</p>
          <RouterLink to="/about" class="yq-btn yq-btn-dark">Learn More</RouterLink>
        </div>
        <div class="about-media">
          <img v-if="aboutImage" :src="aboutImage" alt="YQ Metals production facility" />
          <div v-else class="media-placeholder"></div>

        </div>
      </div>
    </section>

    <!-- Stats -->
    <section v-if="!loading" class="stats-section">
      <div class="yq-container stats-grid">
        <div v-for="s in stats" :key="s.label" class="stat-card">
          <strong>{{ s.value }}</strong>
          <span>{{ s.label }}</span>
        </div>
      </div>
    </section>

    <!-- Products -->
    <section v-if="!loading" class="products-section">
      <div class="yq-container">
        <div class="section-head">
          <div>
            <span class="yq-kicker">Our Products</span>
            <h2>Built for demanding industrial projects</h2>
          </div>
          <RouterLink to="/products" class="section-link">View All Products →</RouterLink>
        </div>
        <div class="product-grid">
          <RouterLink v-for="(cat, i) in categories" :key="cat.id" :to="`/products?cat=${cat.id}`" class="product-card">
            <div class="product-media">
              <img v-if="cat.imageUrl" :src="cat.imageUrl" :alt="cat.nameEn" loading="lazy" />
              <div v-else class="media-placeholder"></div>
              <span class="product-index">0{{ i + 1 }}</span>
            </div>
            <div class="product-body">
              <h3>{{ cat.nameEn }}</h3>
              <p>{{ cat.description || "Engineered metal products for industrial and construction use." }}</p>
              <span class="product-link">View details →</span>
            </div>
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- Quality -->
    <section v-if="!loading" class="quality-section">
      <div class="quality-bg" :style="qualityImage ? { backgroundImage: `url(${qualityImage})` } : {}"></div>
      <div class="quality-overlay"></div>
      <div class="yq-container quality-inner">
        <div class="quality-head">
          <span class="yq-kicker">Quality Control</span>
          <h2>{{ s("home.quality.title", "Quality first. Service foremost.") }}</h2>
        </div>
        <div class="quality-copy">
          <p>{{ s("home.quality.desc", "Delivering high-quality metal products is the heart of what we do. Every batch passes dimensional, coating and load checks before shipment, backed by full traceability from raw material to delivery.") }}</p>
          <RouterLink to="/factory" class="yq-btn yq-btn-ghost">View Our Factory</RouterLink>
        </div>
      </div>
    </section>

    <!-- News -->
    <section v-if="!loading" class="news-section">
      <div class="yq-container">
        <div class="section-head">
          <div>
            <span class="yq-kicker">Latest Information</span>
            <h2>News &amp; Insights</h2>
          </div>
          <RouterLink to="/news" class="section-link">View All News →</RouterLink>
        </div>
        <div v-if="recentNews.length" class="news-layout">
          <div class="news-featured-col">
            <RouterLink :to="`/news/${recentNews[0].id}`" class="news-featured">
              <div class="news-featured-media">
                <img v-if="recentNews[0].coverImageUrl" :src="recentNews[0].coverImageUrl" :alt="recentNews[0].title" loading="lazy" />
                <div v-else class="media-placeholder"></div>
              </div>
              <div class="news-featured-body">
                <h3>{{ recentNews[0].title }}</h3>
                <span class="news-date">{{ formatDate(recentNews[0].publishDate) }}</span>
              </div>
            </RouterLink>
          </div>
          <div class="news-list-col">
            <RouterLink v-for="n in recentNews.slice(1, 4)" :key="n.id" :to="`/news/${n.id}`" class="news-item">
              <div class="news-item-media">
                <img v-if="n.coverImageUrl" :src="n.coverImageUrl" :alt="n.title" loading="lazy" />
                <div v-else class="media-placeholder"></div>
              </div>
              <div class="news-item-body">
                <span class="news-tag">{{ n.category }}</span>
                <h4>{{ n.title }}</h4>
                <span class="news-date">{{ formatDate(n.publishDate) }}</span>
              </div>
            </RouterLink>
          </div>
        </div>
        <div v-else class="news-empty">News is being updated. Please check back soon.</div>
      </div>
    </section>

    <!-- FAQ -->
    <section v-if="!loading" class="faq-section">
      <div class="yq-container faq-grid">
        <div class="faq-media">
          <img v-if="faqImage" :src="faqImage" alt="YQ Metals production line" loading="lazy" />
          <div v-else class="media-placeholder"></div>

        </div>
        <div class="faq-content">
          <span class="yq-kicker">FAQ</span>
          <h2>Frequently Asked Questions</h2>
          <div class="faq-list">
            <details v-for="(f, i) in faqs" :key="i" class="faq-item">
              <summary>{{ f.q }}</summary>
              <p>{{ f.a }}</p>
            </details>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section v-if="!loading" class="cta-section">
      <div class="yq-container cta-inner">
        <div>
          <span class="yq-kicker cta-kicker">Get in touch</span>
          <h2>{{ s("home.cta.title", "Ready to work with us?") }}</h2>
          <p>{{ s("home.cta.desc", "Send your specification and quantity — our team replies within 24 hours.") }}</p>
        </div>
        <RouterLink to="/about" class="yq-btn cta-btn">Request a Quote</RouterLink>
      </div>
    </section>

    <PublicFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { RouterLink } from "vue-router";
import PublicHeader from "../../components/PublicHeader.vue";
import PublicFooter from "../../components/PublicFooter.vue";
import { breadcrumb, faqPage, organization, setJsonLd } from "../../composables/useMeta";
import { api } from "../../api";

const categories = ref<any[]>([]);
const recentNews = ref<any[]>([]);
const factoryPhotos = ref<any[]>([]);
const pageHeroes = ref<any[]>([]);
const settings = ref<Record<string, string>>({});
const loading = ref(true);

const s = (key: string, fallback: string) => settings.value[key] || fallback;

const heroImage = computed(() => { const h = pageHeroes.value.find((p: any) => p.page === "home"); return h?.imageUrl || ""; });
const heroTitle = computed(() => settings.value["hero.title"] || "Professional Metal Grating & Wire Mesh Solutions");
const heroSubtitle = computed(() => settings.value["hero.subtitle"] || "Trusted by builders and contractors worldwide since 2005.");

const advantages = computed(() => [
  { title: s("home.why.1.title", "Complete manufacturing capability"), desc: s("home.why.1.desc", "CNC cutting, automated welding and hot-dip galvanizing under one roof.") },
  { title: s("home.why.2.title", "Quality traceability"), desc: s("home.why.2.desc", "Dimensional, coating and load checks on every production batch.") },
  { title: s("home.why.3.title", "20 years of export experience"), desc: s("home.why.3.desc", "Serving buyers worldwide since 2005.") },
  { title: s("home.why.4.title", "OEM / ODM support"), desc: s("home.why.4.desc", "Custom sizes, finishes and packing for your project specifications.") },
]);

const aboutImage = computed(() => { const h = pageHeroes.value.find((p: any) => p.type === "home-img" && p.page === "about"); return h?.imageUrl || ""; });
const qualityImage = computed(() => {
  const configured = pageHeroes.value.find((p: any) => p.type === "home-img" && p.page === "quality");
  return configured?.imageUrl || settings.value["home.quality.image"] || factoryPhotos.value[1]?.imageUrl || "";
});
const faqImage = computed(() => { const h = pageHeroes.value.find((p: any) => p.type === "home-img" && p.page === "faq"); return h?.imageUrl || ""; });

const stats = computed(() => [
  { value: s("home.stats.years", "18+"), label: s("home.stats.years_label", "Years of Experience") },
  { value: s("home.stats.area", "20K m²"), label: s("home.stats.area_label", "Production Area") },
  { value: s("home.stats.countries", "50+"), label: s("home.stats.countries_label", "Export Countries") },
  { value: s("home.stats.cert", "ISO"), label: s("home.stats.cert_label", "Quality System") },
]);

const faqs = computed(() => [
  { q: s("home.faq.1.q", "Where is your factory located?"), a: s("home.faq.1.a", "Our factory is in No. 229, Chengnan Industrial Park, Anping County, Hebei, China — the hometown of wire mesh.") },
  { q: s("home.faq.2.q", "What products do you manufacture?"), a: s("home.faq.2.a", "We manufacture steel grating and galvanized steel wire, plus custom sizes on request.") },
  { q: s("home.faq.3.q", "How do you control quality?"), a: s("home.faq.3.a", "Every batch goes through dimensional, coating and load inspection before packing and shipment.") },
  { q: s("home.faq.4.q", "Can you support custom specifications?"), a: s("home.faq.4.a", "Yes. Send drawings or samples and we will confirm material, finish, tolerance and lead time.") },
]);

onMounted(async () => {
  try {
    settings.value = await api.settings.publicGet();
  } catch {
    settings.value = {};
  }
  try {
    categories.value = await api.categories.tree();
  } catch {
    categories.value = [];
  }
  try {
    recentNews.value = (await api.news.list()).slice(0, 4);
  } catch {
    recentNews.value = [];
  }
  try {
    factoryPhotos.value = (await api.factoryImages.list()).filter((p: any) => p.imageUrl);
    try { const allPageHeroes = await api.pageHeroes.getAll(); pageHeroes.value = allPageHeroes; } catch { pageHeroes.value = []; }
    // Also load home-img type images from the public factory-images endpoint (no auth needed)
    try {
      const homeImgs = await api.factoryImages.listByType("home-img");
      if (homeImgs.length > 0) pageHeroes.value = pageHeroes.value.concat(homeImgs);
    } catch { }
    factoryPhotos.value = [];
  } catch {
    factoryPhotos.value = [];
  }
  loading.value = false;
  setJsonLd([organization(settings.value), faqPage(faqs.value), breadcrumb(["Home"], "/")]);
});

const formatDate = (d: string) => new Date(d).toLocaleDateString("en-US", { year: "numeric", month: "short", day: "numeric" });
</script>

<style scoped>
.media-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(rgba(255,255,255,0.05) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.05) 1px, transparent 1px), linear-gradient(135deg, #1c2024, #2b3238);
  background-size: 32px 32px, 32px 32px, cover;
}

.loading-state { min-height: 400px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 16px; padding: 80px 0; }
.skeleton { background: linear-gradient(90deg, var(--yq-gray-100) 25%, var(--yq-gray-200) 50%, var(--yq-gray-100) 75%); background-size: 200% 100%; animation: shimmer 1.5s infinite; border-radius: 4px; }
.hero-skel { width: 600px; max-width: 80%; height: 48px; }
.line-skel { width: 400px; max-width: 60%; height: 16px; }
.line-skel.short { width: 280px; }
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }

/* Hero */
.hero { position: relative; min-height: 640px; display: flex; align-items: center; overflow: hidden; background: var(--yq-steel-900); color: #fff; }
.hero-bg { position: absolute; inset: 0; background-color: var(--yq-steel-900); background-size: cover; background-position: center; }
.hero-overlay { position: absolute; inset: 0; background: linear-gradient(90deg, rgba(14,16,18,0.9) 0%, rgba(14,16,18,0.62) 50%, rgba(14,16,18,0.25) 100%); }
.hero-grid { position: absolute; inset: 0; }
.hero-inner { position: relative; z-index: 2; padding-top: 110px; padding-bottom: 110px; }
.hero-kicker { color: #e8b4b4; }
.hero-kicker::before { background: var(--yq-red-light); }
.hero h1 { font-size: clamp(40px, 5.6vw, 84px); font-weight: 800; line-height: 0.96; color: #fff; margin: 20px 0 22px; max-width: 820px; }
.hero p { font-size: 17px; line-height: 1.7; color: rgba(255,255,255,0.78); max-width: 560px; margin-bottom: 34px; }
.hero-actions { display: flex; gap: 12px; flex-wrap: wrap; }


/* Why */
.why-section { background: #fff; color: var(--yq-gray-800); padding: 96px 0; border-bottom: 1px solid var(--yq-gray-100); }
.why-section .yq-kicker { color: var(--yq-red); }
.why-section .yq-kicker::before { background: var(--yq-red); }
.why-head { margin-bottom: 48px; }
.why-head-inner { display: flex; align-items: baseline; gap: 16px; flex-wrap: wrap; }
.why-badge { display: inline-flex; align-items: center; gap: 8px; font-size: 12px; font-weight: 700; letter-spacing: 0.14em; text-transform: uppercase; color: var(--yq-red); background: rgba(189,40,50,0.1); border: 1px solid rgba(189,40,50,0.25); padding: 5px 14px; border-radius: 100px; white-space: nowrap; }
.why-badge::before { content: '"\25CE"'; display: inline-block; width: 6px; height: 6px; border-radius: 50%; background: var(--yq-red); flex-shrink: 0; }
.why-title { font-size: clamp(32px, 4vw, 52px); font-weight: 800; color: var(--yq-ink); margin: 0; line-height: 1.05; letter-spacing: -0.025em; }
.why-title span { color: var(--yq-red); }
.why-lead { color: var(--yq-gray-600); max-width: 580px; margin: 16px 0 0; line-height: 1.7; font-size: 15px; }
.why-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.why-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 20px;
}
.why-card {
  background: var(--yq-gray-50);
  color: var(--yq-gray-800);
  padding: 32px 32px 36px;
  position: relative;
  transition: background 0.25s ease, color 0.2s ease, transform 0.25s ease, box-shadow 0.25s ease;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-height: 250px;
  border: 1px solid var(--yq-gray-200);
  box-shadow: none;
}
.why-card:hover { background: var(--yq-red); color: #fff; transform: translateY(-6px); box-shadow: 0 8px 30px rgba(189,40,50,0.25); border-color: var(--yq-red); }
.why-icon-wrap {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid var(--yq-gray-200);
  display: flex;
  align-items: center;
  justify-content: flex-start;
  color: var(--yq-red);
  transition: border-color 0.25s ease, color 0.25s ease, transform 0.25s ease;
}
.why-card:hover .why-icon-wrap { border-color: rgba(255,255,255,0.5); color: #fff; transform: scale(1.08); }
.why-icon { width: 24px; height: 24px; }
.why-num-wrap { display: flex; align-items: flex-start; gap: 12px; margin-bottom: 14px; }
.why-main { margin-bottom: 10px; }
.why-card h3 {
  font-size: 26px;
  font-weight: 700;
  font-size: 28px;
  margin: 0;
  transition: color 0.2s;
  line-height: 1.2;
  letter-spacing: -0.02em;
}
.why-card:hover h3 { color: #fff; }
.why-line {
  width: 36px;
  height: 2px;
  background: var(--yq-red);
  margin: 10px 0 14px;
  transition: background 0.25s ease, width 0.3s ease;
}
.why-card:hover .why-line { background: rgba(255,255,255,0.4); width: 52px; }
.why-num {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 700;
  line-height: 1;
  color: var(--yq-gray-300);
  letter-spacing: -0.01em;
  transition: color 0.25s ease;
}
.why-num .underscore { font-weight: 400; opacity: 0.4; }
.why-card:hover .why-num { color: rgba(255,255,255,0.5); }
.why-card p { font-size: 13.5px; line-height: 1.65; color: var(--yq-gray-600); transition: color 0.2s; flex: 1; }
.why-card:hover p { color: rgba(255,255,255,0.85); }


.about-section { background: linear-gradient(180deg, #eef3ee 0%, #f5f8f5 100%); padding: 96px 0; }
.about-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 64px; align-items: center; }
.about-copy h2 { font-size: clamp(28px, 3.2vw, 42px); margin: 12px 0 18px; }
.about-copy p { color: var(--yq-gray-600); line-height: 1.75; margin-bottom: 16px; }
.about-copy .yq-btn-dark { margin-top: 12px; }
.about-media { position: relative; aspect-ratio: 4/3; overflow: hidden; background: var(--yq-steel-800); }
.about-media img { width: 100%; height: 100%; object-fit: cover; }


/* Stats */
.stats-section { background: #fff; padding: 0 0 88px; display: flex; align-items: center; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.stat-card { border: 1px solid var(--yq-gray-200); padding: 28px 24px; }
.stat-card strong { display: block; font-family: var(--font-display); font-size: 40px; font-weight: 800; line-height: 1; color: var(--yq-gray-900); }
.stat-card span { display: block; margin-top: 8px; font-size: 11px; letter-spacing: 0.1em; text-transform: uppercase; color: var(--yq-gray-400); }

/* Products */
.products-section { background: linear-gradient(180deg, #eef3ee 0%, #f5f8f5 100%); padding: 88px 0; }
.section-head { display: flex; align-items: flex-end; justify-content: space-between; gap: 24px; margin-bottom: 40px; }
.section-head h2 { font-size: clamp(28px, 3.2vw, 40px); margin-top: 12px; max-width: 640px; }
.section-link { font-size: 12px; font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; color: var(--yq-red); white-space: nowrap; }
.product-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.product-card { display: block; background: #fff; border: 1px solid var(--yq-gray-200); transition: border-color 0.2s, transform 0.2s; }
.product-card:hover { border-color: var(--yq-red); transform: translateY(-4px); }
.product-media { position: relative; aspect-ratio: 4/3; background: var(--yq-steel-800); overflow: hidden; }
.product-media img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.35s; }
.product-card:hover .product-media img { transform: scale(1.04); }
.product-index { position: absolute; top: 12px; left: 12px; background: var(--yq-red); color: #fff; font-size: 11px; font-weight: 700; padding: 3px 8px; }
.product-body { padding: 20px; border-top: 3px solid var(--yq-red); }
.product-body h3 { font-size: 20px; margin-bottom: 8px; }
.product-body p { font-size: 13px; color: var(--yq-gray-600); line-height: 1.6; min-height: 42px; }
.product-link { display: inline-block; margin-top: 12px; font-size: 11px; font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; color: var(--yq-red); }

/* Quality */
.quality-section { position: relative; min-height: 460px; display: flex; align-items: center; overflow: hidden; background: var(--yq-steel-900); color: #fff; }
.quality-bg { position: absolute; inset: 0; background-size: cover; background-position: center; }
.quality-overlay { position: absolute; inset: 0; background: linear-gradient(90deg, rgba(14,16,18,0.92) 0%, rgba(14,16,18,0.6) 55%, rgba(14,16,18,0.3) 100%); }
.quality-inner { position: relative; z-index: 2; display: grid; grid-template-columns: 1fr 1fr; gap: 64px; align-items: center; padding-top: 88px; padding-bottom: 88px; }
.quality-section .yq-kicker { color: #e8b4b4; }
.quality-section .yq-kicker::before { background: var(--yq-red-light); }
.quality-head h2 { font-size: clamp(28px, 3.2vw, 42px); color: #fff; margin-top: 12px; }
.quality-copy p { color: rgba(255,255,255,0.72); line-height: 1.75; margin-bottom: 24px; }

/* News */
.news-section { background: #fff; padding: 88px 0; }
.news-layout { display: flex; gap: 28px; }
.news-featured-col { flex: 1.25; min-width: 0; }
.news-list-col { flex: 1; display: flex; flex-direction: column; gap: 28px; }
.news-featured { display: flex; flex-direction: column; border: 1px solid var(--yq-gray-200); transition: border-color 0.2s; }
.news-featured:hover { border-color: var(--yq-red); }
.news-featured-media { flex: 1; min-height: 0; max-height: 268px; background: var(--yq-gray-200); overflow: hidden; }
.news-featured-media img { width: 100%; height: 100%; object-fit: cover; }
.news-featured-body { padding: 22px 24px 26px; border-top: 1px solid var(--yq-gray-200); }
.news-featured-body h3 { font-size: 24px; margin: 0 0 10px; line-height: 1.25; }
.news-item { display: flex; gap: 14px; border: 1px solid var(--yq-gray-200); padding: 12px 14px; transition: border-color 0.2s; }
.news-item:hover { border-color: var(--yq-red); }
.news-item-media { width: 100px; height: 75px; flex-shrink: 0; background: var(--yq-gray-200); overflow: hidden; }
.news-item-media img { width: 100%; height: 100%; object-fit: cover; }
.news-item-body { flex: 1; min-width: 0; }
.news-item-body h4 { font-size: 16px; margin: 6px 0; line-height: 1.3; }

.news-tag { font-size: 11px; font-weight: 700; letter-spacing: 0.1em; text-transform: uppercase; color: var(--yq-red); }
.news-date { font-size: 12px; color: var(--yq-gray-400); }
.news-empty { text-align: center; color: #888; padding: 48px; border: 1px dashed var(--yq-gray-200); }

/* FAQ */
.faq-section { background: linear-gradient(180deg, #eef3ee 0%, #f5f8f5 100%); padding: 96px 0; }
.faq-grid { display: grid; grid-template-columns: 1fr 1.1fr; gap: 64px; align-items: center; }
.faq-media { position: relative; aspect-ratio: 4/5; max-height: 520px; overflow: hidden; background: var(--yq-steel-800); }
.faq-media img { width: 100%; height: 100%; object-fit: cover; }
.faq-content h2 { font-size: clamp(28px, 3.2vw, 42px); margin: 12px 0 24px; }
.faq-list { border-top: 1px solid var(--yq-gray-200); }
.faq-item { border-bottom: 1px solid var(--yq-gray-200); }
.faq-item summary { cursor: pointer; padding: 18px 0; font-size: 15px; font-weight: 600; color: var(--yq-gray-900); list-style: none; position: relative; padding-right: 28px; transition: color 0.2s ease; }
.faq-item summary:hover { color: var(--yq-red); }
.faq-item summary::-webkit-details-marker { display: none; }
.faq-item summary::after { content: "+"; position: absolute; right: 0; top: 16px; color: var(--yq-red); font-size: 18px; }
.faq-item[open] summary::after { content: "\2212"; }
.faq-item p { padding: 0 0 18px; font-size: 14px; line-height: 1.7; color: var(--yq-gray-600); }

/* CTA */
.cta-section { background: var(--yq-red); color: #fff; padding: 64px 0; }
.cta-inner { display: flex; align-items: center; justify-content: space-between; gap: 32px; }
.cta-kicker { color: rgba(255,255,255,0.8); }
.cta-kicker::before { background: #fff; }
.cta-inner h2 { color: #fff; font-size: clamp(28px, 3.2vw, 40px); margin: 10px 0 8px; }
.cta-inner p { color: rgba(255,255,255,0.85); }
.cta-btn { background: #fff; color: var(--yq-red); flex-shrink: 0; }
.cta-btn:hover { background: var(--yq-ink); color: #fff; }

@media (max-width: 1024px) {
  .why-grid { grid-template-columns: 1fr 1fr; }
  .about-grid, .quality-inner, .faq-grid { grid-template-columns: 1fr; gap: 40px; }
  .stats-grid { grid-template-columns: 1fr 1fr; }
  .product-grid { grid-template-columns: 1fr 1fr; }
  .faq-media { aspect-ratio: 16/10; max-height: none; }
}
@media (max-width: 720px) {
  .hero { min-height: 0; }
  .hero-inner { padding-top: 80px; padding-bottom: 90px; }

  .why-section, .products-section, .news-section, .about-section, .faq-section { padding: 64px 0; }
  .why-grid, .stats-grid, .product-grid { grid-template-columns: 1fr; }
  .section-head { flex-direction: column; align-items: flex-start; }
  .cta-inner { flex-direction: column; align-items: flex-start; }
}
</style>