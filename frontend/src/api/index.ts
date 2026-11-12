const BASE = '/api'
const tokenKey = 'yqmetals_token'
const userKey = 'yqmetals_user'
const roleKey = 'yqmetals_role'
export const MAX_IMAGE_UPLOAD_SIZE = 3 * 1024 * 1024
export const IMAGE_SIZE_LIMIT_MESSAGE = '图片太大，请控制在3MB以内上传'

export const getToken = (): string | null => localStorage.getItem(tokenKey)
export const setToken = (token: string): void => localStorage.setItem(tokenKey, token)
export const clearToken = (): void => localStorage.removeItem(tokenKey)

function redirectToLogin(): void {
  clearToken()
  localStorage.removeItem(userKey)
  localStorage.removeItem(roleKey)
  if (window.location.pathname !== '/admin/login') {
    window.location.replace('/admin/login?expired=1')
  }
}

function errorMessage(text: string, fallback: string): string {
  if (!text) return fallback
  try {
    const payload = JSON.parse(text)
    return payload.error || payload.message || fallback
  } catch {
    return text
  }
}

async function request<T = any>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getToken()
  const headers = new Headers(options.headers)
  headers.set('Content-Type', 'application/json')
  if (token) headers.set('Authorization', 'Bearer ' + token)

  let response: Response
  try {
    response = await fetch(BASE + path, { ...options, headers })
  } catch {
    throw new Error('连接失败')
  }

  if (!response.ok) {
    const text = await response.text().catch(() => '')
    if (response.status === 401) {
      redirectToLogin()
    }
    throw new Error(errorMessage(text, 'HTTP ' + response.status))
  }
  if (response.status === 204) return undefined as T

  const text = await response.text()
  return (text ? JSON.parse(text) : undefined) as T
}

export const api = {
  auth: {
    login: (data: any) =>
      request('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  },
  upload: {
    image: async (file: File): Promise<any> => {
      if (file.size > MAX_IMAGE_UPLOAD_SIZE) {
        throw new Error(IMAGE_SIZE_LIMIT_MESSAGE)
      }
      const form = new FormData()
      form.append('file', file)
      const token = getToken()

      let response: Response
      try {
        response = await fetch(BASE + '/admin/uploads/image', {
          method: 'POST',
          headers: token ? { Authorization: 'Bearer ' + token } : {},
          body: form,
        })
      } catch {
        throw new Error('连接失败')
      }

      if (!response.ok) {
        const text = await response.text().catch(() => '')
        if (response.status === 401) {
          redirectToLogin()
          return new Promise<never>(() => {})
        }
        if (response.status === 413) {
          throw new Error(IMAGE_SIZE_LIMIT_MESSAGE)
        }
        throw new Error(errorMessage(text, '上传失败 HTTP ' + response.status))
      }
      return response.json()
    },
  },
  inquiries: {
    publicSubmit: (data: any) =>
      request('/inquiries/public/submit', { method: 'POST', body: JSON.stringify(data) }),
    getAll: () => request('/inquiries'),
    getMine: () => request('/inquiries/mine'),
    assign: (id: number, assigneeId: number) =>
      request('/inquiries/' + id + '/assign', {
        method: 'PUT',
        body: JSON.stringify({ assigneeId }),
      }),
    stats: () => request('/inquiries/stats'),
    availableYears: () => request('/inquiries/stats/years'),
  },
  categories: {
    tree: () => request('/categories/tree'),
    forFactory: () => request('/factory-images/for-factory'),
    children: (parentId: number) => request('/categories/' + parentId + '/children'),
    create: (data: any) =>
      request('/categories', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: any) =>
      request('/categories/' + id, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: number) => request('/categories/' + id, { method: 'DELETE' }),
    listByType: (type: string) => request('/categories/list/' + type),
    params: {
      listAll: () => request('/params'),
      create: (data: any) =>
        request('/params', { method: 'POST', body: JSON.stringify(data) }),
      update: (id: number, data: any) =>
        request('/params/' + id, { method: 'PUT', body: JSON.stringify(data) }),
      delete: (id: number) => request('/params/' + id, { method: 'DELETE' }),
      toggle: (id: number) => request('/params/' + id + '/toggle', { method: 'PUT' }),
      getAssignedCategories: (paramId: number) =>
        request('/params/categories/list?paramId=' + paramId),
    },
  },
  products: {
    list: () => request('/products'),
    byCategory: (categoryId: number) => request('/products/category/' + categoryId),
    get: (id: number) => request('/products/' + id),
    create: (data: any) =>
      request('/products', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: any) =>
      request('/products/' + id, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: number) => request('/products/' + id, { method: 'DELETE' }),
  },
  news: {
    list: () => request('/news'),
    byCategory: (category: string) => request('/news/category/' + category),
    get: (id: number) => request('/news/' + id),
    create: (data: any) =>
      request('/news', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: any) =>
      request('/news/' + id, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: number) => request('/news/' + id, { method: 'DELETE' }),
  },
  users: {
    list: () => request('/admin/users'),
    create: (data: any) =>
      request('/admin/users', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: any) =>
      request('/admin/users/' + id, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: number) => request('/admin/users/' + id, { method: 'DELETE' }),
    resetPassword: (id: number, password: string) =>
      request('/admin/users/' + id + '/password', {
        method: 'PUT',
        body: JSON.stringify({ password }),
      }),
  },
  settings: {
    get: () => request('/admin/settings'),
    save: (data: any) =>
      request('/admin/settings', { method: 'PUT', body: JSON.stringify(data) }),
    publicGet: () => request('/settings/public'),
  },
  auditLogs: {
    list: (params: {
      page?: number
      size?: number
      username?: string
      action?: string
    }) => {
      const query = new URLSearchParams()
      if (params.page) query.set('page', String(params.page))
      if (params.size) query.set('size', String(params.size))
      if (params.username) query.set('username', params.username)
      if (params.action) query.set('action', params.action)
      return request('/admin/audit-logs?' + query.toString())
    },
  },
  factoryImages: {
    forFactory: () => request('/factory-images/for-factory'),
    list: () => request('/factory-images'),
    adminList: () => request('/admin/factory-images'),
    listByType: (type: string) => request('/factory-images?type=' + type),
    create: (data: any) =>
      request('/admin/factory-images', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: any) =>
      request('/admin/factory-images/' + id, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    delete: (id: number) =>
      request('/admin/factory-images/' + id, { method: 'DELETE' }),
  },
  pageHeroes: {
    getAll: () => request('/page-heroes'),
    getByPage: (page: string) => request('/page-heroes/' + page),
  },
  homeImages: {
    list: () => request('/home-images'),
    adminList: () => request('/home-images/admin'),
  },
}
