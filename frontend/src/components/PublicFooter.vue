<template>
  <footer class="site-footer">
    <div class="footer-inner">
      <div class="footer-brand">
        <a href="/" class="footer-logo-link"><img v-if="logoUrl" :src="logoUrl" alt="Yuanquan Metals" class="footer-logo-img" /><span v-else class="footer-logo-text"><span class="mark">YQ</span> Yuanquan Metals</span></a>
        <p class="footer-name">{{ companyName }}</p>
        <p class="footer-desc">Professional metal grating and wire mesh manufacturer since 2005.</p>
      </div>
      <div class="footer-col">
        <h4>Products</h4>
        <RouterLink v-for="cat in productCats" :key="cat.id" :to="`/products?cat=${cat.id}`">{{ cat.nameEn }}</RouterLink>
        <RouterLink to="/products" v-if="!productCats.length">All Products</RouterLink>
      </div>
      <div class="footer-col">
        <h4>Company</h4>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/products">Products</RouterLink>
        <RouterLink to="/factory">Factory</RouterLink>
        <RouterLink to="/news">News</RouterLink>
        <RouterLink to="/about">About Us</RouterLink>
      </div>
      <div class="footer-col">
        <h4>Contact</h4>
        <span v-if="addressLines.length" class="footer-address">{{ addressLines[0] }}<br v-if="addressLines[1]" />{{ addressLines[1] }}</span>
        <span v-if="phone">Tel: {{ phone }}</span>
        <span>Email: sales@yqwiremesh.com</span>
      </div>
    </div>
    <div class="footer-bottom">
      <span>&copy; {{ year }} {{ companyName }} All rights reserved.</span>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { RouterLink } from "vue-router";
import { api } from "../api";

const settings = ref<Record<string, string>>({});
const categories = ref<any[]>([]);
const year = new Date().getFullYear();
const logoUrl = ref<string>("");

const addressLines = computed(() => {
  const full = (settings.value["company.address"] || "No. 229, Chengnan Industrial Park, Anping County, Hebei, China").trim();
  const marker = ", Anping County";
  const idx = full.indexOf(marker);
  if (idx > 0) return [full.slice(0, idx + 1), full.slice(idx + 2)];
  return [full];
});
const phone = computed(() => settings.value["company.phone"] || "");
const companyName = computed(() => settings.value["company.name"] || "Anping Yuanquan Metals Int&apos;l Co., Ltd.");
const productCats = computed(() => categories.value.filter((c: any) => !c.parentId));

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
    const logos = await api.factoryImages.listByType("logo");
    if (logos.length > 0) logoUrl.value = logos[0].imageUrl;
  } catch {}
});
</script>

<style scoped>
.site-footer { background: var(--yq-steel-900); color: #9aa3aa; padding: 56px 0 0; }
.footer-inner {
  max-width: var(--yq-container);
  margin: 0 auto;
  padding: 0 24px 48px;
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1.3fr;
  gap: 32px;
}
.footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 14px;
}
.footer-logo .mark {
  width: 32px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--yq-red);
  color: #fff;
  font-size: 14px;
  border-radius: 2px;
}
.footer-logo-img { width: 48px; height: 48px; object-fit: contain; border-radius: 4px; display: block; }
.footer-name { font-size: 13px; color: #c4cad0; margin-bottom: 6px; }
.footer-desc { font-size: 13px; line-height: 1.6; color: #7c858d; max-width: 320px; }
.footer-col h4 {
  font-family: var(--font-sans);
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  margin-bottom: 14px;
}
.footer-col a,
.footer-col span {
  display: block;
  font-size: 13px;
  color: #8b949c;
  text-decoration: none;
  padding: 4px 0;
  word-break: break-word;
}
.footer-col a:hover { color: #fff; }
.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: 18px 24px;
  text-align: center;
  font-size: 12px;
  color: #6b747c;
}
@media (max-width: 900px) {
  .footer-inner { grid-template-columns: 1fr 1fr; }
}
@media (max-width: 560px) {
  .footer-inner { grid-template-columns: 1fr; }
}
</style>
