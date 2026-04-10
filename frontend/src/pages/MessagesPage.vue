<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '@/api'
import type { Conversation, Message } from '@/lib/types'

const route = useRoute()

const loading = ref(false)
const error = ref<string | null>(null)

const conversations = ref<Conversation[]>([])
const selectedConversationId = ref<string | null>(null)
const messages = ref<Message[]>([])
const messageInput = ref('')
const sending = ref(false)

async function loadConversations() {
  loading.value = true
  error.value = null
  try {
    conversations.value = await api.listConversations({ limit: 30 })
    const qid = typeof route.query.conversationId === 'string' ? route.query.conversationId : null
    selectedConversationId.value = qid || conversations.value[0]?.id || null
  } catch (e: any) {
    error.value = e?.message ?? '加载失败'
  } finally {
    loading.value = false
  }
}

async function loadMessages() {
  if (!selectedConversationId.value) {
    messages.value = []
    return
  }
  try {
    messages.value = await api.listMessages(selectedConversationId.value, { limit: 200 })
  } catch (e: any) {
    error.value = e?.message ?? '加载消息失败'
  }
}

async function send() {
  if (!selectedConversationId.value) {
    return
  }
  const content = messageInput.value.trim()
  if (!content) {
    return
  }
  sending.value = true
  try {
    await api.sendMessage(selectedConversationId.value, { content })
    messageInput.value = ''
    await loadMessages()
  } catch (e: any) {
    error.value = e?.message ?? '发送失败'
  } finally {
    sending.value = false
  }
}

watch(
  () => selectedConversationId.value,
  () => {
    loadMessages()
  },
)

onMounted(async () => {
  await loadConversations()
  await loadMessages()
})
</script>

<template>
  <div class="space-y-3">
    <div class="rounded-xl border border-zinc-200 bg-white p-4">
      <div class="text-lg font-semibold">消息</div>
      <div class="mt-1 text-sm text-zinc-500">会话列表与聊天窗口（文本消息）。</div>
    </div>

    <div v-if="error" class="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
      {{ error }}
    </div>

    <div class="grid gap-3 lg:grid-cols-3">
      <div class="rounded-xl border border-zinc-200 bg-white p-3">
        <div class="mb-2 text-sm font-medium">会话</div>
        <div class="max-h-[480px] space-y-2 overflow-auto pr-1">
          <button
            v-for="c in conversations"
            :key="c.id"
            class="w-full rounded-md border px-3 py-2 text-left text-sm hover:bg-zinc-50"
            :class="c.id === selectedConversationId ? 'border-zinc-900 bg-zinc-50' : 'border-zinc-200 bg-white'"
            @click="selectedConversationId = c.id"
          >
            <div class="font-medium">{{ c.peerNickname ?? c.peerUserId }}</div>
            <div class="mt-1 text-xs text-zinc-500">{{ c.listingTitle ?? '商品' }}</div>
          </button>
        </div>
      </div>

      <div class="lg:col-span-2 rounded-xl border border-zinc-200 bg-white p-3">
        <div class="mb-2 text-sm font-medium">聊天</div>
        <div class="max-h-[420px] space-y-2 overflow-auto rounded-md border border-zinc-200 bg-zinc-50 p-3">
          <div v-for="m in messages" :key="m.id" class="rounded-md bg-white px-3 py-2 text-sm">
            <div class="text-[11px] text-zinc-500">{{ m.createdAt ?? '' }}</div>
            <div class="mt-1 whitespace-pre-line">{{ m.content }}</div>
          </div>
          <div v-if="!messages.length" class="text-sm text-zinc-500">暂无消息</div>
        </div>

        <div class="mt-3 flex gap-2">
          <input v-model="messageInput" class="flex-1 rounded-md border border-zinc-200 px-3 py-2 text-sm outline-none focus:border-zinc-400" placeholder="输入消息…" />
          <button class="rounded-md bg-zinc-900 px-3 py-2 text-sm text-white hover:bg-zinc-800 disabled:opacity-60" :disabled="sending" @click="send">
            发送
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

