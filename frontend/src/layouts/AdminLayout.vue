<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="sidebar-logo">YQ Admin</div>
      <nav class="sidebar-nav">
        <RouterLink to="/admin/dashboard" class="nav-item" :class="{ active: $route.path === '/admin/dashboard' }">Dashboard</RouterLink>
        <RouterLink to="/admin/inquiries" class="nav-item" :class="{ active: $route.path.startsWith('/admin/inquiries') }">Inquiries</RouterLink>
        <RouterLink v-if="isAdmin" to="/admin/categories" class="nav-item" :class="{ active: $route.path.startsWith('/admin/categories') }">Categories</RouterLink>
        <RouterLink v-if="isAdmin" to="/admin/products" class="nav-item" :class="{ active: $route.path.startsWith('/admin/products') }">Products</RouterLink>
        <RouterLink v-if="isAdmin" to="/admin/news" class="nav-item" :class="{ active: $route.path.startsWith('/admin/news') }">News</RouterLink>
        <RouterLink v-if="isAdmin" to="/admin/users" class="nav-item" :class="{ active: $route.path.startsWith('/admin/users') }">Users</RouterLink>
      </nav>
      <div class="sidebar-footer">
        <span>{{ user?.displayName }} ({{ user?.role }})</span>
        <button @click="logout" class="logout-btn">Logout</button>
      </div>
    </aside>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { getToken, clearToken } from '../api'

const router = useRouter()
const token = getToken()
const isAdmin = computed(() => !token || localStorage.getItem('yqmetals_role') === 'ADMIN')
const user = computed(() => JSON.parse(localStorage.getItem('yqmetals_user') || 'null'))

function logout() {
  clearToken()
  localStorage.removeItem('yqmetals_user')
  localStorage.removeItem('yqmetals_role')
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-layout { display: grid; grid-template-columns: 220px 1fr; min-height: 100vh; }
.sidebar { background: var(--yq-gray-900); color: #ccc; padding: 20px 0; display: flex; flex-direction: column; }
.sidebar-logo { font-size: 18px; font-weight: 700; color: #fff; padding: 0 20px 20px; border-bottom: 1px solid #333; }
.sidebar-nav { flex: 1; padding: 12px 0; }
.nav-item { display: block; padding: 10px 20px; color: #aaa; text-decoration: none; font-size: 14px; transition: all 0.15s; }
.nav-item:hover, .nav-item.active { background: rgba(255,255,255,0.08); color: #fff; }
.nav-item.active { border-left: 3px solid var(--yq-red); }
.sidebar-footer { padding: 16px 20px; border-top: 1px solid #333; font-size: 12px; }
.sidebar-footer span { display: block; margin-bottom: 8px; }
.logout-btn { background: transparent; border: 1px solid #555; color: #aaa; padding: 4px 12px; border-radius: 4px; font-size: 12px; cursor: pointer; }
.logout-btn:hover { border-color: var(--yq-red); color: var(--yq-red); }
.main-content { background: var(--yq-gray-50); padding: 24px; overflow-y: auto; }
@media (max-width: 768px) { .admin-layout { grid-template-columns: 1fr; } .sidebar { display: none; } }
</style>
