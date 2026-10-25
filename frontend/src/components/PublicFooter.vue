<template>
  <footer class="site-footer">
    <div class="footer-inner">
      <div class="footer-col">
        <div class="logo-footer">YQ Metals</div>
        <p class="footer-desc">Anping YQ Metals Int&apos;l Co., Ltd.<br />Professional metal grid and wire mesh manufacturer since 2005.</p>
      </div>
      <div class="footer-col">
        <h4>Products</h4>
        <RouterLink to="/products">Steel Grating</RouterLink>
        <RouterLink to="/products">Galvanized Steel Wire</RouterLink>
        <RouterLink to="/products">Metal Guardrail</RouterLink>
      </div>
      <div class="footer-col">
        <h4>Markets</h4>
        <span>Southeast Asia</span>
        <span>Europe</span>
        <span>Middle East</span>
      </div>
      <div class="footer-col">
        <h4>Contact</h4>
        <span>Hebei, Anping, China</span>
        <span>Email: sophia@yqwiremesh.com</span>
      </div>
    </div>
    <div class="footer-bottom">
      <span>&copy; {{ new Date().getFullYear() }} Anping YQ Metals Int&apos;l Co., Ltd. All rights reserved.</span>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<style scoped>
.site-footer { background: var(--yq-gray-900); color: #ccc; padding: 48px 0 0; }
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px 48px;
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 32px;
}
.logo-footer { font-size: 20px; font-weight: 700; color: #fff; margin-bottom: 12px; }
.footer-desc { font-size: 13px; line-height: 1.6; color: #999; }
.footer-col h4 { color: #fff; font-size: 14px; margin-bottom: 12px; }
.footer-col a, .footer-col span {
  display: block;
  font-size: 13px;
  color: #888;
  text-decoration: none;
  padding: 4px 0;
}
.footer-col a:hover { color: var(--yq-red-light); }
.footer-bottom {
  border-top: 1px solid #333;
  padding: 16px 24px;
  text-align: center;
  font-size: 12px;
  color: #666;
}
</style>
