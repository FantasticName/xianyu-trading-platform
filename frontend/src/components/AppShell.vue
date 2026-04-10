<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MessageCircle, PlusCircle, User } from 'lucide-vue-next'
import { useAuth } from '@/lib/auth'

const route = useRoute()
const router = useRouter()
const { token, clearToken } = useAuth()

const isAuthed = computed(() => Boolean(token.value))

function go(path: string) {
  router.push(path)
}

function logout() {
  clearToken()
  router.replace('/login')
}

const showShell = computed(() => route.path !== '/login')
</script>

<template>
  <div class="min-h-dvh bg-zinc-50 text-zinc-900">
    <header v-if="showShell" class="sticky top-0 z-10 border-b border-zinc-200 bg-white/90 backdrop-blur">
      <div class="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-3">
        <button class="text-sm font-semibold hover:opacity-80" @click="go('/')">闲鱼交易平台</button>
        <div class="flex items-center gap-2">
          <button class="inline-flex items-center gap-2 rounded-md px-3 py-2 text-sm hover:bg-zinc-100" @click="go('/publish')">
            <PlusCircle class="h-4 w-4" />
            发布
          </button>
          <button class="inline-flex items-center gap-2 rounded-md px-3 py-2 text-sm hover:bg-zinc-100" @click="go('/messages')">
            <MessageCircle class="h-4 w-4" />
            消息
          </button>
          <button class="inline-flex items-center gap-2 rounded-md px-3 py-2 text-sm hover:bg-zinc-100" @click="go('/me')">
            <User class="h-4 w-4" />
            我的
          </button>
          <button v-if="isAuthed" class="rounded-md border border-zinc-200 px-3 py-2 text-sm hover:bg-zinc-100" @click="logout">
            退出
          </button>
          <button v-else class="rounded-md border border-zinc-200 px-3 py-2 text-sm hover:bg-zinc-100" @click="go('/login')">
            登录
          </button>
        </div>
      </div>
    </header>

    <main class="mx-auto max-w-6xl px-4 py-6">
      <slot />
    </main>
  </div>
</template>

