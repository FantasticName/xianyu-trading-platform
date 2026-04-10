<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { api } from '@/api'
import { useAuth } from '@/lib/auth'
import type { Listing, Me, UserPublic } from '@/lib/types'

const { clearToken } = useAuth()

const loading = ref(false)
const error = ref<string | null>(null)
const me = ref<Me | null>(null)

const nickname = ref('')
const avatarUrl = ref('')

const myListings = ref<Listing[]>([])
const myFollows = ref<UserPublic[]>([])
const myFavorites = ref<Listing[]>([])

const saving = ref(false)

const isAdmin = computed(() => me.value?.role === 'ADMIN')
const adminListingId = ref('')
const adminCommentId = ref('')
const adminBusy = ref(false)

async function load() {
  loading.value = true
  error.value = null
  try {
    me.value = await api.getMe()
    nickname.value = me.value.nickname
    avatarUrl.value = me.value.avatarUrl ?? ''
    myListings.value = await api.listMyListings({ limit: 50 })
    myFollows.value = await api.listMyFollows({ limit: 50 })
    myFavorites.value = await api.listMyFavorites({ limit: 50 })
  } catch (e: any) {
    error.value = e?.message ?? '加载失败'
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  saving.value = true
  error.value = null
  try {
    me.value = await api.updateMe({ nickname: nickname.value, avatarUrl: avatarUrl.value || null })
  } catch (e: any) {
    error.value = e?.message ?? '保存失败'
  } finally {
    saving.value = false
  }
}

function logout() {
  clearToken()
  window.location.href = '/login'
}

async function adminDeleteListing() {
  if (!adminListingId.value.trim()) {
    return
  }
  adminBusy.value = true
  error.value = null
  try {
    await api.adminDeleteListing({ listingId: adminListingId.value.trim() })
    adminListingId.value = ''
    await load()
  } catch (e: any) {
    error.value = e?.message ?? '操作失败'
  } finally {
    adminBusy.value = false
  }
}

async function adminDeleteComment() {
  if (!adminCommentId.value.trim()) {
    return
  }
  adminBusy.value = true
  error.value = null
  try {
    await api.adminDeleteComment({ commentId: adminCommentId.value.trim() })
    adminCommentId.value = ''
  } catch (e: any) {
    error.value = e?.message ?? '操作失败'
  } finally {
    adminBusy.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="space-y-4">
    <div class="rounded-xl border border-zinc-200 bg-white p-4">
      <div class="text-lg font-semibold">个人中心</div>
      <div class="mt-1 text-sm text-zinc-500">个人资料、我发布的商品、关注与收藏。</div>
    </div>

    <div v-if="error" class="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
      {{ error }}
    </div>

    <div v-if="loading" class="h-48 animate-pulse rounded-xl border border-zinc-200 bg-white" />

    <div v-else class="grid gap-4 lg:grid-cols-3">
      <div class="rounded-xl border border-zinc-200 bg-white p-4">
        <div class="text-sm font-medium">我的资料</div>
        <div v-if="me" class="mt-3 space-y-3">
          <div class="text-xs text-zinc-500">账号：{{ me.account }} · 角色：{{ me.role }}</div>
          <label class="block">
            <div class="mb-1 text-xs text-zinc-600">昵称</div>
            <input v-model="nickname" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
          </label>
          <label class="block">
            <div class="mb-1 text-xs text-zinc-600">头像 URL（可选）</div>
            <input v-model="avatarUrl" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
          </label>
          <div class="flex gap-2">
            <button class="rounded-md bg-zinc-900 px-3 py-2 text-sm text-white hover:bg-zinc-800 disabled:opacity-60" :disabled="saving" @click="saveProfile">
              保存
            </button>
            <button class="rounded-md border border-zinc-200 px-3 py-2 text-sm hover:bg-zinc-100" @click="logout">退出登录</button>
          </div>
        </div>
      </div>

      <div class="lg:col-span-2 space-y-4">
        <div class="rounded-xl border border-zinc-200 bg-white p-4">
          <div class="text-sm font-medium">我发布的商品</div>
          <div class="mt-3 grid grid-cols-1 gap-2 sm:grid-cols-2">
            <div v-for="l in myListings" :key="l.id" class="rounded-md border border-zinc-200 px-3 py-2">
              <div class="text-sm font-medium">{{ l.title }}</div>
              <div class="mt-1 text-xs text-zinc-500">￥{{ l.price }} · {{ l.status }}</div>
              <div class="mt-1 text-[11px] text-zinc-500">{{ l.id }}</div>
            </div>
            <div v-if="!myListings.length" class="text-sm text-zinc-500">暂无发布</div>
          </div>
        </div>

        <div class="grid gap-4 sm:grid-cols-2">
          <div class="rounded-xl border border-zinc-200 bg-white p-4">
            <div class="text-sm font-medium">我的关注</div>
            <div class="mt-3 space-y-2">
              <div v-for="u in myFollows" :key="u.id" class="rounded-md border border-zinc-200 px-3 py-2 text-sm">
                {{ u.nickname }}
                <div class="text-[11px] text-zinc-500">{{ u.id }}</div>
              </div>
              <div v-if="!myFollows.length" class="text-sm text-zinc-500">暂无关注</div>
            </div>
          </div>

          <div class="rounded-xl border border-zinc-200 bg-white p-4">
            <div class="text-sm font-medium">我的收藏</div>
            <div class="mt-3 space-y-2">
              <div v-for="l in myFavorites" :key="l.id" class="rounded-md border border-zinc-200 px-3 py-2 text-sm">
                {{ l.title }}
                <div class="text-[11px] text-zinc-500">{{ l.id }}</div>
              </div>
              <div v-if="!myFavorites.length" class="text-sm text-zinc-500">暂无收藏</div>
            </div>
          </div>
        </div>

        <div v-if="isAdmin" class="rounded-xl border border-zinc-200 bg-white p-4">
          <div class="text-sm font-medium">管理员工具</div>
          <div class="mt-2 text-xs text-zinc-500">输入 ID 快速删除违规商品/评论（一期最小实现）。</div>
          <div class="mt-3 grid gap-3 sm:grid-cols-2">
            <label class="block">
              <div class="mb-1 text-xs text-zinc-600">删除商品 listingId</div>
              <input v-model="adminListingId" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
              <button class="mt-2 rounded-md bg-zinc-900 px-3 py-2 text-sm text-white hover:bg-zinc-800 disabled:opacity-60" :disabled="adminBusy" @click="adminDeleteListing">
                删除商品
              </button>
            </label>
            <label class="block">
              <div class="mb-1 text-xs text-zinc-600">删除评论 commentId</div>
              <input v-model="adminCommentId" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
              <button class="mt-2 rounded-md bg-zinc-900 px-3 py-2 text-sm text-white hover:bg-zinc-800 disabled:opacity-60" :disabled="adminBusy" @click="adminDeleteComment">
                删除评论
              </button>
            </label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

