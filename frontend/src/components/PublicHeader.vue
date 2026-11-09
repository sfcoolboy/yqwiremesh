<template>
  <header class="site-header" :class="{ scrolled, 'mobile-open': menuOpen }">
    <div class="header-inner">
      <RouterLink to="/" class="logo">
        <img v-if="logoUrl" :src="logoUrl" alt="Yuanquan Metals" class="logo-img" />
        <template v-else>
          <span class="logo-mark">YQ</span>
        </template>
        <span class="logo-lockup">
          <strong>{{ tradingName }}</strong>
          <small>{{ productSubtitle }}</small>
        </span>
      </RouterLink>
      <nav class="nav" :class="{ open: menuOpen }" aria-label="Main navigation">
        <RouterLink to="/" class="nav-link" @click="menuOpen = false">Home</RouterLink>
        <RouterLink to="/products" class="nav-link" @click="menuOpen = false">Products</RouterLink>
        <RouterLink to="/factory" class="nav-link" @click="menuOpen = false">Factory</RouterLink>
        <RouterLink to="/news" class="nav-link" @click="menuOpen = false">News</RouterLink>
        <RouterLink to="/about" class="nav-link" @click="menuOpen = false">About</RouterLink>
        <RouterLink to="/about" class="nav-cta mobile-cta" @click="menuOpen = false">Request a quote</RouterLink>
      </nav>
      <div class="header-actions">
        <RouterLink to="/about" class="nav-cta">Request a quote</RouterLink>
      </div>
      <button class="menu-toggle" :aria-label="menuOpen ? 'Close menu' : 'Open menu'" :aria-expanded="menuOpen" @click="menuOpen = !menuOpen">
        <span class="bar" /><span class="bar" /><span class="bar" />
      </button>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { RouterLink } from 'vue-router'
import { api } from '../api'

const menuOpen = ref(false)
const logoUrl = ref('')
const scrolled = ref(false)
const settings = ref<Record<string, string>>({})
const tradingName = computed(() => settings.value['company.trading.name'] || 'YQ Metals')
const productSubtitle = computed(() => settings.value['company.products'] || 'Steel Grating · Galvanized Wire')

const onScroll = () => { scrolled.value = window.scrollY > 10 }

function onKeyDown(e: KeyboardEvent) {
  if (e.key === 'Escape' && menuOpen.value) menuOpen.value = false
}

onMounted(() => {
  window.addEventListener('scroll', onScroll, { passive: true })
  document.addEventListener('keydown', onKeyDown)
})

onMounted(async () => {
  try {
    const logos = await api.factoryImages.listByType('logo')
    if (logos.length > 0) logoUrl.value = logos[0].imageUrl
  } catch {}
})
onMounted(async () => {
  try { settings.value = await api.settings.publicGet() } catch { settings.value = {} }
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  document.removeEventListener('keydown', onKeyDown)
})
</script>

<style scoped>
.site-header { background: #fff; border-bottom: 1px solid var(--yq-gray-200); position: sticky; top: 0; z-index: 100; transition: box-shadow 0.2s; }
.site-header.scrolled { box-shadow: 0 1px 8px rgba(0,0,0,0.08); }
.header-inner { width: 1248px; max-width: 100%; margin: 0 auto; padding: 0 24px; box-sizing: border-box; display: flex; align-items: center; justify-content: space-between; gap: 32px; height: 72px; }
.logo { display: flex; align-items: center; gap: 12px; text-decoration: none; flex-shrink: 0; }
.logo-img { width: 48px; height: 48px; object-fit: contain; border-radius: 4px; }
.logo-mark { width: 40px; height: 40px; background: var(--yq-red); color: #fff; display: flex; align-items: center; justify-content: center; font-family: var(--font-display); font-size: 18px; font-weight: 800; border-radius: 2px; }
.logo-lockup { display: flex; flex-direction: column; line-height: 1.1; }
.logo-lockup strong { font-family: var(--font-display); font-size: 18px; font-weight: 800; letter-spacing: 0; white-space: nowrap; color: var(--yq-gray-900); }
.logo-lockup small { margin-top: 3px; font-size: 9px; letter-spacing: 0.14em; text-transform: uppercase; color: var(--yq-gray-400); }
.nav { display: flex; align-items: center; gap: 56px; justify-self: center; }
.nav-link { position: relative; padding: 6px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.1em; text-transform: uppercase; text-decoration: none; color: var(--yq-gray-800); white-space: nowrap; transition: color 0.15s; }
.nav-link::after { content: ''; position: absolute; left: 0; right: 0; bottom: 0; height: 2px; background: var(--yq-red); transform: scaleX(0); transform-origin: left; transition: transform 0.18s; }
.nav-link:hover, .nav-link.router-link-exact-active { color: var(--yq-red); }
.nav-link:hover::after, .nav-link.router-link-exact-active::after { transform: scaleX(1); }
.header-actions { display: flex; align-items: center; justify-content: flex-end; flex-shrink: 0; }
.nav-cta { display: inline-flex; align-items: center; justify-content: center; background: var(--yq-red); color: #fff; padding: 12px 28px; font-size: 12px; font-weight: 700; letter-spacing: 0.06em; text-transform: uppercase; border-radius: 2px; white-space: nowrap; text-decoration: none; transition: background 0.18s; }
.nav-cta:hover { background: var(--yq-red-light); }
.mobile-cta { display: none; }
.menu-toggle { display: none; flex-direction: column; gap: 5px; background: none; border: none; cursor: pointer; padding: 8px; margin-left: auto; }
.menu-toggle .bar { display: block; width: 22px; height: 2px; background: var(--yq-gray-800); transition: transform 0.2s, opacity 0.2s; }
.menu-toggle[aria-expanded='true'] .bar:nth-child(1) { transform: translateY(7px) rotate(45deg); }
.menu-toggle[aria-expanded='true'] .bar:nth-child(2) { opacity: 0; }
.menu-toggle[aria-expanded='true'] .bar:nth-child(3) { transform: translateY(-7px) rotate(-45deg); }
@media (max-width: 1100px) { .nav { gap: 30px; } }
@media (max-width: 980px) {
  .header-inner { display: flex; flex-wrap: wrap; align-items: center; height: auto; padding: 12px 20px; gap: 0; }
  .logo { order: 1; width: auto; margin-right: auto; }
  .logo-lockup small { display: none; }
  .header-actions { display: none; }
  .menu-toggle { order: 2; display: flex; margin-left: 12px; }
  .nav { order: 3; display: none; width: 100%; flex-direction: column; align-items: stretch; gap: 0; padding: 8px 0 16px; background: #fff; border-top: 1px solid var(--yq-gray-200); margin-left: 0; }
  .nav.open { display: flex; }
  .nav-link { padding: 14px 0; border-bottom: 1px solid var(--yq-gray-100); font-size: 14px; }
  .nav-link::after { display: none; }
  .mobile-cta { display: flex; margin-top: 12px; padding: 14px; border-radius: 4px; }
}
</style>
