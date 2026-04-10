<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/api'
import ListingCard from '@/components/ListingCard.vue'
import type { Listing } from '@/lib/types'

const router = useRouter()

const loading = ref(false)
const error = ref<string | null>(null)
const listings = ref<Listing[]>([])

const keyword = ref('')
const category = ref('')
const minPrice = ref<string>('')
const maxPrice = ref<string>('')

async function load() {
  loading.value = true
  error.value = null
  try {
    listings.value = await api.searchListings({
      keyword: keyword.value || undefined,
      category: category.value || undefined,
      minPrice: minPrice.value ? Number(minPrice.value) : undefined,
      maxPrice: maxPrice.value ? Number(maxPrice.value) : undefined,
      page: 1,
      pageSize: 24,
    })
  } catch (e: any) {
    error.value = e?.message ?? '加载失败'
  } finally {
    loading.value = false
  }
}

async function recommend() {
  loading.value = true
  error.value = null
  try {
    listings.value = await api.recommendListings({ limit: 24 })
  } catch (e: any) {
    error.value = e?.message ?? '加载失败'
  } finally {
    loading.value = false
  }
}

function openListing(id: string) {
  router.push(`/listing/${id}`)
}

onMounted(() => {
  load()
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-col gap-3 rounded-xl border border-zinc-200 bg-white p-4 sm:flex-row sm:items-end">
      <label class="block flex-1">
        <div class="mb-1 text-xs text-zinc-600">关键词</div>
        <input v-model="keyword" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
      </label>
      <label class="block w-full sm:w-44">
        <div class="mb-1 text-xs text-zinc-600">分类</div>
        <input v-model="category" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" placeholder="例如 数码" />
      </label>
      <label class="block w-full sm:w-32">
        <div class="mb-1 text-xs text-zinc-600">最低价</div>
        <input v-model="minPrice" inputmode="decimal" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
      </label>
      <label class="block w-full sm:w-32">
        <div class="mb-1 text-xs text-zinc-600">最高价</div>
        <input v-model="maxPrice" inputmode="decimal" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
      </label>
      <div class="flex gap-2">
        <button class="rounded-md bg-zinc-900 px-3 py-2 text-sm text-white hover:bg-zinc-800" @click="load">搜索</button>
        <button class="rounded-md border border-zinc-200 px-3 py-2 text-sm hover:bg-zinc-100" @click="recommend">随机推荐</button>
      </div>
    </div>

    <div v-if="error" class="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
      {{ error }}
    </div>

    <div v-if="loading" class="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
      <div v-for="n in 8" :key="n" class="h-64 animate-pulse rounded-lg border border-zinc-200 bg-white" />
    </div>

    <div v-else class="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
      <div v-for="it in listings" :key="it.id" @click="openListing(it.id)">
        <ListingCard :listing="it" />
      </div>
    </div>
  </div>
</template>

