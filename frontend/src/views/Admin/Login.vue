<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-header">
        <span class="logo-mark">YQ</span>
        <h1>管理后台</h1>
        <p>Anping Yuanquan Metals Int&apos;l Co., Ltd.</p>
      </div>
      <form @submit.prevent="login">
        <div class="field">
          <label>用户名</label>
          <input v-model="username" type="text" required autofocus placeholder="请输入用户名" />
        </div>
        <div class="field">
          <label>密码</label>
          <input v-model="password" type="password" required placeholder="请输入密码" />
        </div>
        <button type="submit" class="btn-login" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
        <p v-if="error" class="error">{{ error }}</p>
      </form>
      <RouterLink to="/" class="back-link">返回官网</RouterLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api, setToken } from '../../api'

const router = useRouter()
const route = useRoute()
const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref(route.query.expired ? '登录状态已失效，请重新登录' : '')

async function login() {
  loading.value = true
  error.value = ''
  try {
    const res = await api.auth.login({ username: username.value, password: password.value })
    setToken(res.token)
    localStorage.setItem('yqmetals_user', JSON.stringify(res.user))
    localStorage.setItem('yqmetals_role', res.user.role)
    router.push('/admin/dashboard')
  } catch (e: any) {
    error.value = e.message === 'HTTP 401' ? '用户名或密码错误' : '登录失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: var(--yq-gray-100); }
.login-box { background: #fff; padding: 40px; border-radius: 8px; width: 100%; max-width: 380px; box-shadow: 0 4px 24px rgba(0,0,0,0.08); }
.login-header { text-align: center; margin-bottom: 28px; }
.logo-mark { display: inline-flex; width: 48px; height: 48px; background: var(--yq-red); color: #fff; font-size: 18px; font-weight: 800; align-items: center; justify-content: center; border-radius: 6px; margin-bottom: 12px; }
.login-header h1 { font-size: 22px; margin-bottom: 4px; }
.login-header p { font-size: 13px; color: #888; }
.field { margin-bottom: 14px; }
.field label { display: block; font-size: 13px; font-weight: 600; color: var(--yq-gray-800); margin-bottom: 4px; }
.field input { width: 100%; padding: 10px 12px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 14px; }
.field input:focus { outline: none; border-color: var(--yq-red); }
.btn-login { width: 100%; background: var(--yq-red); color: #fff; border: none; padding: 12px; border-radius: 4px; font-size: 15px; font-weight: 600; cursor: pointer; }
.btn-login:hover { background: var(--yq-red-light); }
.btn-login:disabled { opacity: 0.6; }
.error { color: red; font-size: 13px; margin-top: 8px; }
.back-link { display: block; text-align: center; margin-top: 20px; font-size: 13px; color: #888; text-decoration: none; }
.back-link:hover { color: var(--yq-red); }
</style>
