import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  // Public site
  { path: '/', name: 'Home', component: () => import('../views/Public/Home.vue') },
  { path: '/products', name: 'Products', component: () => import('../views/Public/Products.vue') },
  { path: '/product/:id', name: 'ProductDetail', component: () => import('../views/Public/ProductDetail.vue') },
  { path: '/factory', name: 'Factory', component: () => import('../views/Public/Factory.vue') },
  { path: '/news', name: 'News', component: () => import('../views/Public/News.vue') },
  { path: '/news/:id', name: 'NewsDetail', component: () => import('../views/Public/NewsDetail.vue') },
  { path: '/about', name: 'About', component: () => import('../views/Public/About.vue') },

  // Admin
  { path: '/admin', redirect: '/admin/dashboard' },
  { path: '/admin/login', name: 'AdminLogin', component: () => import('../views/Admin/Login.vue') },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Admin/Dashboard.vue') },
      { path: 'inquiries', name: 'Inquiries', component: () => import('../views/Admin/Inquiries.vue') },
      { path: 'categories', name: 'Categories', component: () => import('../views/Admin/Categories.vue') },
      { path: 'products', name: 'Products', component: () => import('../views/Admin/Products.vue') },
      { path: 'news', name: 'News', component: () => import('../views/Admin/News.vue') },
      { path: 'users', name: 'Users', component: () => import('../views/Admin/Users.vue') },
    ]
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

export default router
