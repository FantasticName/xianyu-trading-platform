import { apiDelete, apiGet, apiPatch, apiPost } from '@/lib/http'
import type {
  AuthToken,
  Comment,
  Conversation,
  Listing,
  ListingDetail,
  Me,
  Message,
  UserPublic,
} from '@/lib/types'

export const api = {
  register: (payload: { account: string; password: string; nickname: string; role?: 'USER' | 'ADMIN'; adminInviteCode?: string }) =>
    apiPost<AuthToken>('/auth/register', payload),
  login: (payload: { account: string; password: string }) => apiPost<AuthToken>('/auth/login', payload),

  getMe: () => apiGet<Me>('/me'),
  updateMe: (payload: { nickname: string; avatarUrl?: string | null }) => apiPatch<Me>('/me', payload),
  searchUsers: (params: { keyword: string; limit?: number }) => apiGet<UserPublic[]>('/users', params),

  searchListings: (params: {
    keyword?: string
    category?: string
    minPrice?: number
    maxPrice?: number
    page?: number
    pageSize?: number
  }) => apiGet<Listing[]>('/listings', params),
  recommendListings: (params?: { limit?: number }) => apiGet<Listing[]>('/listings/recommend', params),
  getListingDetail: (id: string) => apiGet<ListingDetail>(`/listings/${id}`),
  createListing: (payload: {
    title: string
    category: string
    price: number
    condition: 'NEW' | 'LIKE_NEW' | 'GOOD' | 'FAIR'
    description: string
    imageUrls?: string[]
  }) => apiPost<Listing>('/listings', payload),
  updateListing: (id: string, payload: Partial<{ title: string; category: string; price: number; condition: string; description: string; imageUrls: string[]; status: string }>) =>
    apiPatch<Listing>(`/listings/${id}`, payload),
  deleteListing: (id: string) => apiDelete<null>(`/listings/${id}`),

  listMyListings: (params?: { limit?: number }) => apiGet<Listing[]>('/me/listings', params),
  listMyFollows: (params?: { limit?: number }) => apiGet<UserPublic[]>('/me/follows', params),
  listMyFavorites: (params?: { limit?: number }) => apiGet<Listing[]>('/me/favorites', params),

  follow: (payload: { sellerId: string }) => apiPost<null>('/follows', payload),
  unfollow: (payload: { sellerId: string }) => apiDelete<null>('/follows', payload),
  favorite: (payload: { listingId: string }) => apiPost<null>('/favorites', payload),
  unfavorite: (payload: { listingId: string }) => apiDelete<null>('/favorites', payload),

  listComments: (listingId: string, params?: { limit?: number }) => apiGet<Comment[]>(`/listings/${listingId}/comments`, params),
  addComment: (listingId: string, payload: { content: string }) => apiPost<null>(`/listings/${listingId}/comments`, payload),
  deleteComment: (commentId: string) => apiDelete<null>(`/comments/${commentId}`),

  getOrCreateConversation: (payload: { listingId: string }) => apiPost<Conversation>('/conversations', payload),
  listConversations: (params?: { limit?: number }) => apiGet<Conversation[]>('/conversations', params),
  listMessages: (conversationId: string, params?: { limit?: number }) => apiGet<Message[]>(`/conversations/${conversationId}/messages`, params),
  sendMessage: (conversationId: string, payload: { content: string }) => apiPost<null>(`/conversations/${conversationId}/messages`, payload),

  adminDeleteListing: (params: { listingId: string }) => apiDelete<null>('/admin/listings', params),
  adminDeleteComment: (params: { commentId: string }) => apiDelete<null>('/admin/comments', params),
}

