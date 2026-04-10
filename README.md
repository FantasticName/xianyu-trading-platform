# 闲鱼交易平台（Java JDBC + Vue3）

## 功能清单（一期）
- 普通用户注册/登录（BCrypt 加密，JWT 鉴权）
- 首页商品搜索（模糊查询）+ 随机推荐
- 商品详情展示
- 发布/编辑/删除自己的商品
- 关注/取关卖家
- 收藏/取消收藏商品
- 评论发布/删除（本人可删；管理员可删任意评论）
- 会话/消息（文本消息）
- 管理员安全注册（邀请码）+ 删除违规商品/评论

## 技术栈
- 后端：Java 17 + Maven + Servlet + JDBC + MySQL + HikariCP + JWT + BCrypt + SLF4J/Logback
- 前端：Vue3 + Vue Router + Vite + Tailwind + Axios（请求/响应拦截器）

## 目录结构
- `backend/`：后端 Maven 工程（war，内置 Jetty 运行）

- `frontend/`：前端 Vue3 工程

- `建表语句.sql`：MySQL 建表脚本

- `API接口文档.md`：接口文档

  

## 本地启动

### 1) 准备 MySQL
1. 创建数据库并建表：执行根目录 `建表语句.sql`

2. 修改后端配置：`backend/src/main/resources/app.properties`
   - `db.username` / `db.password` / `db.jdbcUrl`
   
     

### 2) 启动后端
在项目根目录执行：
```bash
mvn -f backend/pom.xml jetty:run
```
如果 Maven 本地仓库目录没有写权限（例如报错 `AccessDeniedException: D:\maven\repository`），可改用：
```bash
mvn -s backend/settings.xml -f backend/pom.xml jetty:run
```
默认监听：`http://localhost:8080`

### 3) 启动前端
在 `frontend` 目录执行：
```bash
npm.cmd install
npm.cmd run dev
```
默认地址：`http://localhost:5173`

## 关键实现亮点
- 禁用 SSM/DI：所有依赖通过 `new` + `AppContext` 手动装配
- 统一异常处理：Filter 兜底转换为统一 `Result` JSON
- SQL 注入防御：DAO 层统一使用 `PreparedStatement` 参数绑定
- 事务一致性：Service 层通过 `TxManager.executeInTransaction` 管理事务边界
- JWT 鉴权：Filter 校验 token 并注入 `uid`，Controller/Service 只拿 userId 做业务
