<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { useAuth } from '@/lib/auth'
import type { Comment, ListingDetail } from '@/lib/types'

const route = useRoute()
const router = useRouter()
const { token } = useAuth()

const id = computed(() => String(route.params.id || ''))

const loading = ref(false)
const error = ref<string | null>(null)
const detail = ref<ListingDetail | null>(null)
const comments = ref<Comment[]>([])

const followLoading = ref(false)
const favoriteLoading = ref(false)
const followed = ref(false)
const favorited = ref(false)

const commentInput = ref('')
const commentSubmitting = ref(false)

const isAuthed = computed(() => Boolean(token.value))

async function load() {
  if (!id.value) {
    return
  }
  loading.value = true
  error.value = null
  try {
    detail.value = await api.getListingDetail(id.value)
    comments.value = await api.listComments(id.value, { limit: 50 })
  } catch (e: any) {
    error.value = e?.message ?? '加载失败'
  } finally {
    loading.value = false
  }
}

async function toggleFollow() {
  if (!detail.value) {
    return
  }
  if (!isAuthed.value) {
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  followLoading.value = true
  try {
    if (!followed.value) {
      await api.follow({ sellerId: detail.value.sellerId })
      followed.value = true
    } else {
      await api.unfollow({ sellerId: detail.value.sellerId })
      followed.value = false
    }
  } catch (e: any) {
    error.value = e?.message ?? '操作失败'
  } finally {
    followLoading.value = false
  }
}

async function toggleFavorite() {
  if (!detail.value) {
    return
  }
  if (!isAuthed.value) {
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  favoriteLoading.value = true
  try {
    if (!favorited.value) {
      await api.favorite({ listingId: detail.value.id })
      favorited.value = true
    } else {
      await api.unfavorite({ listingId: detail.value.id })
      favorited.value = false
    }
  } catch (e: any) {
    error.value = e?.message ?? '操作失败'
  } finally {
    favoriteLoading.value = false
  }
}

async function startChat() {
  if (!detail.value) {
    return
  }
  if (!isAuthed.value) {
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  try {
    const conv = await api.getOrCreateConversation({ listingId: detail.value.id })
    router.push({ path: '/messages', query: { conversationId: conv.id } })
  } catch (e: any) {
    error.value = e?.message ?? '发起会话失败'
  }
}

async function submitComment() {
  if (!detail.value) {
    return
  }
  if (!isAuthed.value) {
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (!commentInput.value.trim()) {
    return
  }
  commentSubmitting.value = true
  try {
    await api.addComment(detail.value.id, { content: commentInput.value.trim() })
    commentInput.value = ''
    comments.value = await api.listComments(detail.value.id, { limit: 50 })
  } catch (e: any) {
    error.value = e?.message ?? '发表评论失败'
  } finally {
    commentSubmitting.value = false
  }
}

watch(
  () => id.value,
  () => {
    followed.value = false
    favorited.value = false
    load()
  },
)

onMounted(load)
</script>

<template>
  <div class="space-y-4">
    <div v-if="error" class="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
      {{ error }}
    </div>

    <div v-if="loading" class="h-72 animate-pulse rounded-xl border border-zinc-200 bg-white" />

    <div v-else-if="detail" class="grid gap-4 lg:grid-cols-2">
      <div class="overflow-hidden rounded-xl border border-zinc-200 bg-white">
        <div class="aspect-square bg-zinc-100">
          <img v-if="detail.coverUrl" :src="detail.coverUrl" class="h-full w-full object-cover" />
          <div v-else class="flex h-full w-full items-center justify-center text-xs text-zinc-500">无封面</div>
        </div>
        <div v-if="detail.imageUrls?.length" class="grid grid-cols-5 gap-2 p-3">
          <img v-for="u in detail.imageUrls" :key="u" :src="u" class="aspect-square rounded-md object-cover" />
        </div>
      </div>

      <div class="space-y-3">
        <div class="rounded-xl border border-zinc-200 bg-white p-4">
          <div class="text-lg font-semibold">{{ detail.title }}</div>
          <div class="mt-2 flex items-baseline justify-between">
            <div class="text-xl font-semibold text-red-500">￥{{ detail.price }}</div>
            <div class="text-xs text-zinc-500">{{ detail.category }} · {{ detail.condition }}</div>
          </div>
          <div class="mt-3 flex flex-wrap gap-2">
            <button class="rounded-md bg-zinc-900 px-3 py-2 text-sm text-white hover:bg-zinc-800" @click="startChat">发起私聊</button>
            <button
              class="rounded-md border border-zinc-200 px-3 py-2 text-sm hover:bg-zinc-100 disabled:opacity-60"
              :disabled="followLoading"
              @click="toggleFollow"
            >
              {{ followed ? '取消关注' : '关注卖家' }}
            </button>
            <button
              class="rounded-md border border-zinc-200 px-3 py-2 text-sm hover:bg-zinc-100 disabled:opacity-60"
              :disabled="favoriteLoading"
              @click="toggleFavorite"
            >
              {{ favorited ? '取消收藏' : '收藏商品' }}
            </button>
          </div>
        </div>

        <div class="rounded-xl border border-zinc-200 bg-white p-4">
          <div class="text-sm font-medium">卖家</div>
          <div class="mt-2 flex items-center gap-3">
            <div class="h-10 w-10 overflow-hidden rounded-full bg-zinc-200">
              <img v-if="detail.sellerAvatarUrl" :src="detail.sellerAvatarUrl" class="h-full w-full object-cover" />
            </div>
            <div>
              <div class="text-sm font-medium">{{ detail.sellerNickname ?? detail.sellerId }}</div>
              <div class="text-xs text-zinc-500">点击“发起私聊”沟通交易</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="detail" class="grid gap-4 lg:grid-cols-2">
      <div class="rounded-xl border border-zinc-200 bg-white p-4">
        <div class="text-sm font-medium">商品描述</div>
        <div class="mt-2 whitespace-pre-line text-sm text-zinc-700">{{ detail.description }}</div>
      </div>

      <div class="rounded-xl border border-zinc-200 bg-white p-4">
        <div class="flex items-center justify-between">
          <div class="text-sm font-medium">评论</div>
          <div class="text-xs text-zinc-500">{{ comments.length }} 条</div>
        </div>

        <div class="mt-3 flex gap-2">
          <input
            v-model="commentInput"
            class="flex-1 rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400"
            placeholder="说点什么…"
          />
          <button
            class="rounded-md bg-zinc-900 px-3 py-2 text-sm text-white hover:bg-zinc-800 disabled:opacity-60"
            :disabled="commentSubmitting"
            @click="submitComment"
          >
            发送
          </button>
        </div>

        <div class="mt-4 max-h-80 space-y-3 overflow-auto pr-1">
          <div v-for="c in comments" :key="c.id" class="rounded-md border border-zinc-200 px-3 py-2">
            <div class="flex items-center justify-between">
              <div class="text-xs font-medium text-zinc-700">{{ c.userNickname ?? c.userId }}</div>
              <div class="text-[11px] text-zinc-500">{{ c.createdAt ?? '' }}</div>
            </div>
            <div class="mt-1 whitespace-pre-line text-sm text-zinc-700">{{ c.content }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

