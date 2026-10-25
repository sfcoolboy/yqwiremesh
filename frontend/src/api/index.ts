const BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

const tokenKey = 'yqmetals_token'

export const getToken = () => localStorage.getItem(tokenKey)
export const setToken = (t: string) => localStorage.setItem(tokenKey, t)
export const clearToken = () => localStorage.removeItem(tokenKey)

async function request<T>(path: string, opts: RequestInit = {}): Promise<T> {
  const token = getToken()
  const res = await fetch(BASE + path, {
    ...opts,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...opts.headers,
    },
  })
  if (!res.ok) {
    if (res.status === 401) { clearToken(); window.location.href = '/admin/login' }
    throw new Error(`HTTP ${res.status}`)
  }
  return res.json()
}

export const api = {
  auth: {
    login: (data: { username: string; password: string }) =>
      request<any>('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  },
  inquiries: {
    publicSubmit: (data: any) =>
      request<any>('/inquiries/public/submit', { method: 'POST', body: JSON.stringify(data) }),
    getAll: () => request<any[]>('/inquiries'),
    getMine: () => request<any[]>('/inquiries/mine'),
    assign: (id: number, assigneeId: number) =>
      request<any>(`/inquiries/${id}/assign`, { method: 'PUT', body: JSON.stringify({ assigneeId }) }),
    stats: () => request<any>('/inquiries/stats'),
  },
  categories: {
    tree: () => request<any[]>('/categories/tree'),
    children: (pid: number) => request<any[]>(`/categories/${pid}/children`),
    create: (data: any) => request<any>('/categories', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: any) =>
      request<any>(`/categories/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: number) => request<any>(`/categories/${id}`, { method: 'DELETE' }),
  },
  products: {
    list: () => request<any[]>('/products'),
    byCategory: (cid: number) => request<any[]>(`/products/category/${cid}`),
    get: (id: number) => request<any>(`/products/${id}`),
    create: (data: any) => request<any>('/products', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: any) =>
      request<any>(`/products/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: number) => request<any>(`/products/${id}`, { method: 'DELETE' }),
  },
  news: {
    list: () => request<any[]>('/news'),
    byCategory: (cat: string) => request<any[]>(`/news/category/${cat}`),
    get: (id: number) => request<any>(`/news/${id}`),
    create: (data: any) => request<any>('/news', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: any) =>
      request<any>(`/news/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: number) => request<any>(`/news/${id}`, { method: 'DELETE' }),
  },
  users: {
    list: () => request<any[]>('/admin/users'),
    create: (data: any) => request<any>('/admin/users', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: any) =>
      request<any>(`/admin/users/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: number) => request<any>(`/admin/users/${id}`, { method: 'DELETE' }),
  },
}
