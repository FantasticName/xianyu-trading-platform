# 闲鱼交易平台 API 接口文档（一期）

## 1. 统一约定

### 1.1 Base URL
- 开发环境：`http://localhost:8080/api`

### 1.2 统一返回结构
所有接口返回：
```json
{
  "code": 0,
  "message": "OK",
  "data": {},
  "traceId": "..."
}
```
- `code=0` 表示成功，`data` 为具体数据。
- 非 0 表示失败，`message` 为错误原因。

### 1.3 认证
- 使用 JWT：请求头 `Authorization: Bearer <token>`
- 未登录/过期：HTTP 401

## 2. 鉴权相关

### 2.1 注册
- `POST /auth/register`
- Body：
```json
{ "account": "test@example.com", "password": "123456", "nickname": "小明" }
```
- 管理员安全注册（需要邀请码）：
```json
{ "account": "admin@example.com", "password": "123456", "nickname": "管理员", "role": "ADMIN", "adminInviteCode": "CHANGE_ME" }
```
- Response：
```json
{ "code": 0, "message": "OK", "data": { "token": "..." } }
```

### 2.2 登录
- `POST /auth/login`
- Body：
```json
{ "account": "test@example.com", "password": "123456" }
```
- Response：同注册

## 3. 用户

### 3.1 获取我的信息（需登录）
- `GET /me`

### 3.2 修改我的信息（需登录）
- `PATCH /me`
- Body：
```json
{ "nickname": "新昵称", "avatarUrl": "https://..." }
```

### 3.3 搜索用户（公开）
- `GET /users?keyword=张&limit=20`

### 3.4 我的发布/关注/收藏（需登录）
- `GET /me/listings?limit=50`
- `GET /me/follows?limit=50`
- `GET /me/favorites?limit=50`

## 4. 商品

### 4.1 搜索商品（公开，模糊查询）
- `GET /listings?keyword=&category=&minPrice=&maxPrice=&page=&pageSize=`

### 4.2 随机推荐（公开）
- `GET /listings/recommend?limit=20`

### 4.3 商品详情（公开）
- `GET /listings/{id}`

### 4.4 发布商品（需登录）
- `POST /listings`
- Body：
```json
{
  "title": "九成新键盘",
  "category": "数码",
  "price": 99.0,
  "condition": "GOOD",
  "description": "可小刀",
  "imageUrls": ["https://..."]
}
```

### 4.5 修改/删除自己的商品（需登录）
- `PATCH /listings/{id}`
- `DELETE /listings/{id}`

## 5. 关注/收藏/评论

### 5.1 关注/取关卖家（需登录）
- `POST /follows` Body：`{ "sellerId": "..." }`
- `DELETE /follows` Body：`{ "sellerId": "..." }`

### 5.2 收藏/取消收藏（需登录）
- `POST /favorites` Body：`{ "listingId": "..." }`
- `DELETE /favorites` Body：`{ "listingId": "..." }`

### 5.3 评论
- `GET /listings/{id}/comments?limit=50`（公开）
- `POST /listings/{id}/comments`（需登录）Body：`{ "content": "..." }`
- `DELETE /comments/{commentId}`（需登录；本人或管理员）

## 6. 会话/消息（需登录）

### 6.1 幂等创建或获取会话
- `POST /conversations` Body：`{ "listingId": "..." }`

### 6.2 会话列表
- `GET /conversations?limit=30`

### 6.3 消息列表/发送消息
- `GET /conversations/{id}/messages?limit=200`
- `POST /conversations/{id}/messages` Body：`{ "content": "..." }`

## 7. 管理员接口（需登录且 ADMIN）

- `DELETE /admin/listings?listingId=...`
- `DELETE /admin/comments?commentId=...`

