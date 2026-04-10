import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import LoginPage from '@/pages/LoginPage.vue'
import ListingDetailPage from '@/pages/ListingDetailPage.vue'
import PublishPage from '@/pages/PublishPage.vue'
import MessagesPage from '@/pages/MessagesPage.vue'
import MePage from '@/pages/MePage.vue'
import { useAuth } from '@/lib/auth'

// 定义路由配置
const routes = [
  {
    path: '/',
    name: 'home',
    component: HomePage,
  },
  { path: '/login', name: 'login', component: LoginPage },
  { path: '/listing/:id', name: 'listing', component: ListingDetailPage },
  { path: '/publish', name: 'publish', component: PublishPage, meta: { requiresAuth: true } },
  { path: '/messages', name: 'messages', component: MessagesPage, meta: { requiresAuth: true } },
  { path: '/me', name: 'me', component: MePage, meta: { requiresAuth: true } },
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  if (!to.meta?.requiresAuth) {
    return true
  }
  const { token } = useAuth()
  if (token.value) {
    return true
  }
  return { path: '/login', query: { redirect: to.fullPath } }
})

export default router
