<script setup lang="ts">
import type { Listing } from '@/lib/types'

defineProps<{ listing: Listing }>()
</script>

<template>
  <div class="group cursor-pointer overflow-hidden rounded-lg border border-zinc-200 bg-white hover:shadow-sm">
    <div class="aspect-square bg-zinc-100">
      <img v-if="listing.coverUrl" :src="listing.coverUrl" class="h-full w-full object-cover" />
      <div v-else class="flex h-full w-full items-center justify-center text-xs text-zinc-500">无封面</div>
    </div>
    <div class="space-y-1 p-3">
      <div class="line-clamp-2 text-sm font-medium text-zinc-900">{{ listing.title }}</div>
      <div class="flex items-baseline justify-between gap-3">
        <div class="text-sm font-semibold text-red-500">￥{{ listing.price }}</div>
        <div class="text-xs text-zinc-500">{{ listing.sellerNickname ?? '未知卖家' }}</div>
      </div>
      <div class="text-xs text-zinc-500">{{ listing.category }} · {{ listing.condition }}</div>
    </div>
  </div>
</template>

