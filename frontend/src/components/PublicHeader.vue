<template>
  <header class="site-header">
    <div class="header-inner">
      <RouterLink to="/" class="logo">
        <span class="logo-mark">YQ</span>
        <span class="logo-text">YQ Metals</span>
      </RouterLink>
      <nav class="nav">
        <RouterLink to="/" class="nav-link">Home</RouterLink>
        <RouterLink to="/products" class="nav-link">Products</RouterLink>
        <RouterLink to="/factory" class="nav-link">Factory</RouterLink>
        <RouterLink to="/news" class="nav-link">News</RouterLink>
        <RouterLink to="/about" class="nav-link">About</RouterLink>
      </nav>
      <RouterLink to="/admin/login" class="admin-link">Admin</RouterLink>
    </div>
  </header>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<style scoped>
.site-header {
  background: #fff;
  border-bottom: 1px solid var(--yq-gray-200);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  height: 64px;
  gap: 32px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  font-weight: 700;
  font-size: 18px;
  color: var(--yq-red);
}
.logo-mark {
  width: 36px;
  height: 36px;
  background: var(--yq-red);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 800;
  border-radius: 4px;
}
.nav { display: flex; gap: 24px; flex: 1; }
.nav-link {
  text-decoration: none;
  color: var(--yq-gray-800);
  font-size: 14px;
  font-weight: 500;
  transition: color 0.15s;
}
.nav-link:hover, .nav-link.router-link-active { color: var(--yq-red); }
.admin-link {
  font-size: 13px;
  color: var(--yq-gray-800);
  text-decoration: none;
  opacity: 0.6;
}
.admin-link:hover { opacity: 1; }
</style>
