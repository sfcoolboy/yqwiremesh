import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
import { useMeta } from "../composables/useMeta";

const routes: RouteRecordRaw[] = [
  { path: "/", name: "Home", component: () => import("../views/Public/Home.vue") },
  { path: "/products", name: "Products", component: () => import("../views/Public/Products.vue") },
  { path: "/product/:id", name: "ProductDetail", component: () => import("../views/Public/ProductDetail.vue") },
  { path: "/factory", name: "Factory", component: () => import("../views/Public/Factory.vue") },
  { path: "/news", name: "News", component: () => import("../views/Public/News.vue") },
  { path: "/news/:id", name: "NewsDetail", component: () => import("../views/Public/NewsDetail.vue") },
  { path: "/about", name: "About", component: () => import("../views/Public/About.vue") },
  { path: "/admin/login", name: "AdminLogin", component: () => import("../views/Admin/Login.vue") },
  {
    path: "/admin",
    component: () => import("../layouts/AdminLayout.vue"),
    meta: { public: false },
    children: [
      { path: "", redirect: "/admin/dashboard" },
      { path: "dashboard", name: "Dashboard", component: () => import("../views/Admin/Dashboard.vue") },
      { path: "inquiries", name: "Inquiries", component: () => import("../views/Admin/Inquiries.vue") },
      { path: "categories", name: "Categories", component: () => import("../views/Admin/Categories.vue") },
      { path: "products", name: "AdminProducts", component: () => import("../views/Admin/Products.vue") },
      { path: "news", name: "AdminNews", component: () => import("../views/Admin/News.vue") },
      { path: "image-manager", name: "AdminImageManager", component: () => import("../views/Admin/ImageManager.vue") },
      { path: "users", name: "Users", component: () => import("../views/Admin/Users.vue") },
      { path: "settings", name: "AdminSettings", component: () => import("../views/Admin/Settings.vue") },
      { path: "email-settings", name: "AdminEmailSettings", component: () => import("../views/Admin/EmailSettings.vue") },
      { path: "audit-logs", name: "AdminAuditLogs", component: () => import("../views/Admin/AuditLogs.vue") },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
});

const { updateMeta } = useMeta();

router.beforeEach((to) => {
  const token = localStorage.getItem("yqmetals_token");
  if (to.path.startsWith("/admin") && to.name !== "AdminLogin" && !token) {
    return { name: "AdminLogin", query: { expired: "1" } };
  }
  if (to.name === "AdminLogin" && token) {
    return { name: "Dashboard" };
  }
});

router.afterEach((to) => {
  if ((to.meta as any).public !== false) {
    updateMeta(to);
  }
});

export default router;
