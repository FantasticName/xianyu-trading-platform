import axios, { AxiosError, AxiosHeaders, type AxiosInstance } from 'axios'
import router from '@/router'
import { useAuth } from '@/lib/auth'
import type { ApiResponse } from '@/lib/types'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api'

export const http: AxiosInstance = axios.create({
  baseURL: apiBaseUrl,
  timeout: 15000,
})

http.interceptors.request.use((config) => {
  const { token } = useAuth()
  if (token.value) {
    const headers = AxiosHeaders.from(config.headers)
    headers.set('Authorization', `Bearer ${token.value}`)
    config.headers = headers
  }
  return config
})

http.interceptors.response.use(
  (resp) => {
    const data = resp.data as ApiResponse<unknown>
    if (!data || typeof data.code !== 'number') {
      return resp
    }

    if (data.code === 0) {
      return resp
    }

    const err = new Error(data.message || '请求失败')
    ;(err as any).code = data.code
    ;(err as any).traceId = data.traceId
    throw err
  },
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      const { clearToken } = useAuth()
      clearToken()
      const current = router.currentRoute.value
      const redirect = current.fullPath || '/'
      router.replace({ path: '/login', query: { redirect } })
    }
    throw error
  },
)

export async function apiGet<T>(url: string, params?: Record<string, unknown>) {
  const resp = await http.get<ApiResponse<T>>(url, { params })
  return resp.data.data as T
}

export async function apiPost<T>(url: string, body?: unknown) {
  const resp = await http.post<ApiResponse<T>>(url, body)
  return resp.data.data as T
}

export async function apiPatch<T>(url: string, body?: unknown) {
  const resp = await http.patch<ApiResponse<T>>(url, body)
  return resp.data.data as T
}

export async function apiDelete<T>(url: string, params?: Record<string, unknown>) {
  const resp = await http.delete<ApiResponse<T>>(url, { params })
  return resp.data.data as T
}

