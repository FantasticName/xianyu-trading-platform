<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/api'

const router = useRouter()

const loading = ref(false)
const error = ref<string | null>(null)

const title = ref('')
const category = ref('')
const price = ref('')
const condition = ref<'NEW' | 'LIKE_NEW' | 'GOOD' | 'FAIR'>('GOOD')
const description = ref('')
const imageUrlsText = ref('')

async function submit() {
  error.value = null
  loading.value = true
  try {
    const imageUrls = imageUrlsText.value
      .split(/\n|,/)
      .map((s) => s.trim())
      .filter(Boolean)
    const res = await api.createListing({
      title: title.value,
      category: category.value,
      price: Number(price.value),
      condition: condition.value,
      description: description.value,
      imageUrls,
    })
    router.push(`/listing/${res.id}`)
  } catch (e: any) {
    error.value = e?.message ?? '发布失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="mx-auto max-w-3xl space-y-4">
    <div class="rounded-xl border border-zinc-200 bg-white p-4">
      <div class="text-lg font-semibold">发布商品</div>
      <div class="mt-1 text-sm text-zinc-500">当前版本使用“图片 URL 列表”模拟上传（后续可扩展为真实上传）。</div>
    </div>

    <div v-if="error" class="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
      {{ error }}
    </div>

    <form class="rounded-xl border border-zinc-200 bg-white p-4 space-y-4" @submit.prevent="submit">
      <div class="grid gap-3 sm:grid-cols-2">
        <label class="block">
          <div class="mb-1 text-xs text-zinc-600">标题</div>
          <input v-model="title" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
        </label>
        <label class="block">
          <div class="mb-1 text-xs text-zinc-600">分类</div>
          <input v-model="category" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" placeholder="例如 数码" />
        </label>
        <label class="block">
          <div class="mb-1 text-xs text-zinc-600">价格</div>
          <input v-model="price" inputmode="decimal" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
        </label>
        <label class="block">
          <div class="mb-1 text-xs text-zinc-600">成色</div>
          <select v-model="condition" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400">
            <option value="NEW">全新</option>
            <option value="LIKE_NEW">几乎全新</option>
            <option value="GOOD">良好</option>
            <option value="FAIR">一般</option>
          </select>
        </label>
      </div>

      <label class="block">
        <div class="mb-1 text-xs text-zinc-600">描述</div>
        <textarea v-model="description" rows="6" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
      </label>

      <label class="block">
        <div class="mb-1 text-xs text-zinc-600">图片 URL（用逗号或换行分隔）</div>
        <textarea v-model="imageUrlsText" rows="3" class="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" />
      </label>

      <div class="flex justify-end">
        <button type="submit" class="rounded-md bg-zinc-900 px-4 py-2 text-sm text-white hover:bg-zinc-800 disabled:opacity-60" :disabled="loading">
          {{ loading ? '发布中…' : '发布' }}
        </button>
      </div>
    </form>
  </div>
</template>

