import { computed, ref } from 'vue'

const TOKEN_KEY = 'xianyu_token'

const tokenRef = ref<string | null>(localStorage.getItem(TOKEN_KEY))

export function useAuth() {
  const token = computed(() => tokenRef.value)

  function setToken(token: string) {
    tokenRef.value = token
    localStorage.setItem(TOKEN_KEY, token)
  }

  function clearToken() {
    tokenRef.value = null
    localStorage.removeItem(TOKEN_KEY)
  }

  return {
    token,
    setToken,
    clearToken,
  }
}

