export type ApiResponse<T> = {
  code: number
  message: string
  data: T | null
  traceId?: string
}

export type AuthToken = { token: string }

export type Me = {
  id: string
  account: string
  nickname: string
  avatarUrl: string | null
  role: 'USER' | 'ADMIN'
}

export type UserPublic = {
  id: string
  nickname: string
  avatarUrl: string | null
}

export type Listing = {
  id: string
  sellerId: string
  sellerNickname: string | null
  title: string
  price: number
  condition: 'NEW' | 'LIKE_NEW' | 'GOOD' | 'FAIR'
  category: string
  coverUrl: string | null
  status: 'ACTIVE' | 'OFFLINE'
  createdAt: string | null
}

export type ListingDetail = {
  id: string
  sellerId: string
  sellerNickname: string | null
  sellerAvatarUrl: string | null
  title: string
  price: number
  condition: 'NEW' | 'LIKE_NEW' | 'GOOD' | 'FAIR'
  category: string
  description: string
  coverUrl: string | null
  imageUrls: string[]
  status: 'ACTIVE' | 'OFFLINE'
  createdAt: string | null
}

export type Comment = {
  id: string
  listingId: string
  userId: string
  userNickname: string | null
  userAvatarUrl: string | null
  content: string
  createdAt: string | null
}

export type Conversation = {
  id: string
  listingId: string
  listingTitle: string | null
  peerUserId: string
  peerNickname: string | null
  peerAvatarUrl: string | null
  updatedAt: string | null
}

export type Message = {
  id: string
  conversationId: string
  senderId: string
  content: string
  createdAt: string | null
}

