<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { useAuth } from '@/lib/auth'

type Mode = 'login' | 'register'

const route = useRoute()
const router = useRouter()
const { setToken } = useAuth()

const mode = ref<Mode>('login')
const loading = ref(false)
const error = ref<string | null>(null)

const account = ref('')
const password = ref('')
const nickname = ref('')
const role = ref<'USER' | 'ADMIN'>('USER')
const adminInviteCode = ref('')

const redirect = computed(() => (typeof route.query.redirect === 'string' ? route.query.redirect : '/'))

async function submit() {
  error.value = null
  loading.value = true
  try {
    if (mode.value === 'login') {
      const res = await api.login({ account: account.value, password: password.value })
      setToken(res.token)
      router.replace(redirect.value)
      return
    }

    const res = await api.register({
      account: account.value,
      password: password.value,
      nickname: nickname.value,
      role: role.value,
      adminInviteCode: role.value === 'ADMIN' ? adminInviteCode.value : undefined,
    })
    setToken(res.token)
    router.replace('/')
  } catch (e: any) {
    error.value = e?.message ?? '操作失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="mx-auto max-w-md">
    <div class="rounded-xl border border-zinc-200 bg-white p-6 shadow-sm">
      <div class="mb-4">
        <div class="text-lg font-semibold">闲鱼交易平台</div>
        <div class="text-sm text-zinc-500">{{ mode === 'login' ? '登录后开始浏览与发布' : '创建账号开始使用' }}</div>
      </div>

      <div class="mb-4 flex gap-2">
        <button
          class="flex-1 rounded-md px-3 py-2 text-sm"
          :class="mode === 'login' ? 'bg-zinc-900 text-white' : 'bg-zinc-100 text-zinc-700 hover:bg-zinc-200'"
          @click="mode = 'login'"
        >
          登录
        </button>
        <button
          class="flex-1 rounded-md px-3 py-2 text-sm"
          :class="mode === 'register' ? 'bg-zinc-900 text-white' : 'bg-zinc-100 text-zinc-700 hover:bg-zinc-200'"
          @click="mode = 'register'"
        >
          注册
        </button>
      </div>

      <div v-if="error" class="mb-3 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
        {{ error }}
      </div>

      <form class="space-y-3" @submit.prevent="submit">
        <label class="block">
          <div class="mb-1 text-xs text-zinc-600">账号（邮箱/手机号均可）</div>
          <input v-model="account" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
        </label>
        <label class="block">
          <div class="mb-1 text-xs text-zinc-600">密码</div>
          <input v-model="password" type="password" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
        </label>

        <label v-if="mode === 'register'" class="block">
          <div class="mb-1 text-xs text-zinc-600">昵称</div>
          <input v-model="nickname" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
        </label>

        <div v-if="mode === 'register'" class="grid grid-cols-2 gap-2">
          <label class="block">
            <div class="mb-1 text-xs text-zinc-600">注册角色</div>
            <select v-model="role" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400">
              <option value="USER">普通用户</option>
              <option value="ADMIN">管理员</option>
            </select>
          </label>
          <label v-if="role === 'ADMIN'" class="block">
            <div class="mb-1 text-xs text-zinc-600">管理员邀请码</div>
            <input v-model="adminInviteCode" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
          </label>
        </div>

        <button
          type="submit"
          class="w-full rounded-md bg-zinc-900 px-3 py-2 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
          :disabled="loading"
        >
          {{ loading ? '处理中…' : mode === 'login' ? '登录' : '注册' }}
        </button>
      </form>
    </div>
  </div>
</template>

