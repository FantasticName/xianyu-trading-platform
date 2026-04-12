package io.github.fantasticname.xianyutradingplatform.util;

import io.github.fantasticname.xianyutradingplatform.controller.AdminController;
import io.github.fantasticname.xianyutradingplatform.controller.AuthController;
import io.github.fantasticname.xianyutradingplatform.controller.ConversationController;
import io.github.fantasticname.xianyutradingplatform.controller.ListingController;
import io.github.fantasticname.xianyutradingplatform.controller.SocialController;
import io.github.fantasticname.xianyutradingplatform.controller.UserController;
import io.github.fantasticname.xianyutradingplatform.dao.CommentDao;
import io.github.fantasticname.xianyutradingplatform.dao.ConversationDao;
import io.github.fantasticname.xianyutradingplatform.dao.FavoriteDao;
import io.github.fantasticname.xianyutradingplatform.dao.FollowDao;
import io.github.fantasticname.xianyutradingplatform.dao.ListingDao;
import io.github.fantasticname.xianyutradingplatform.dao.MessageDao;
import io.github.fantasticname.xianyutradingplatform.dao.UserDao;
import io.github.fantasticname.xianyutradingplatform.service.AdminService;
import io.github.fantasticname.xianyutradingplatform.service.AuthService;
import io.github.fantasticname.xianyutradingplatform.service.ConversationService;
import io.github.fantasticname.xianyutradingplatform.service.ListingService;
import io.github.fantasticname.xianyutradingplatform.service.SocialService;
import io.github.fantasticname.xianyutradingplatform.service.UserService;
import io.github.fantasticname.xianyutradingplatform.service.impl.AdminServiceImpl;
import io.github.fantasticname.xianyutradingplatform.service.impl.AuthServiceImpl;
import io.github.fantasticname.xianyutradingplatform.service.impl.ConversationServiceImpl;
import io.github.fantasticname.xianyutradingplatform.service.impl.ListingServiceImpl;
import io.github.fantasticname.xianyutradingplatform.service.impl.SocialServiceImpl;
import io.github.fantasticname.xianyutradingplatform.service.impl.UserServiceImpl;

import javax.sql.DataSource;

/**
 * @author FantasticName
 */
/**
 * 应用上下文（手写 IoC 容器）：集中完成 DAO / Service / Controller 的创建与依赖装配。
 *
 * <p>项目禁用 SSM 框架后，需要一个统一的“对象装配入口”，用于：</p>
 * - 创建全局共享的基础设施（例如 {@link javax.sql.DataSource}）；<br>
 * - 按依赖顺序实例化 DAO、Service，并注入到 Controller；<br>
 * - 向 Servlet 层提供 Controller 的统一访问点，避免在每个请求中重复 new。
 *
 * <p>生命周期与线程安全：</p>
 * - {@link AppContext} 通过静态单例持有，随 Web 应用加载而初始化；<br>
 * - Controller/Service/DAO 都应视为无状态组件：不在实例字段中保存请求级数据，确保并发安全；<br>
 * - 请求级上下文（如当前用户、TraceId、事务连接）通过 request attribute / {@link ThreadLocal} 等机制传递。
 *
 * @author FantasticName
 */


/*
组装顺序严格如下：

创建数据库连接池 DataSource（从 DataSourceProvider 获取）。

用 DataSource 创建各个 Dao（数据访问对象，类似 MyBatis 的 Mapper）。

用 Dao 创建各个 Service（业务逻辑层）。

用 Service 创建各个 Controller。
 */
public final class AppContext {
    private static final AppContext INSTANCE = new AppContext();

    private final DataSource dataSource;

    private final UserDao userDao;
    private final ListingDao listingDao;
    private final FollowDao followDao;
    private final FavoriteDao favoriteDao;
    private final CommentDao commentDao;
    private final ConversationDao conversationDao;
    private final MessageDao messageDao;

    private final AuthService authService;
    private final UserService userService;
    private final ListingService listingService;
    private final SocialService socialService;
    private final ConversationService conversationService;
    private final AdminService adminService;

    private final AuthController authController;
    private final UserController userController;
    private final ListingController listingController;
    private final SocialController socialController;
    private final ConversationController conversationController;
    private final AdminController adminController;

    private AppContext() {
        // 1) 初始化基础设施：连接池 DataSource 全局复用。
        this.dataSource = DataSourceProvider.getInstance().getDataSource();

        // 2) 初始化 DAO：只负责 SQL 与数据映射；连接来源由 DataSource + TxManager 协同决定。
        this.userDao = new UserDao(dataSource);
        this.listingDao = new ListingDao(dataSource);
        this.followDao = new FollowDao(dataSource);
        this.favoriteDao = new FavoriteDao(dataSource);
        this.commentDao = new CommentDao(dataSource);
        this.conversationDao = new ConversationDao(dataSource);
        this.messageDao = new MessageDao(dataSource);

        // 3) 初始化 Service：承载业务流程编排，并在需要时通过 TxManager 维护事务边界。
        this.authService = new AuthServiceImpl(dataSource, userDao);
        this.userService = new UserServiceImpl(dataSource, userDao);
        this.listingService = new ListingServiceImpl(dataSource, listingDao, userDao);
        this.socialService = new SocialServiceImpl(dataSource, followDao, favoriteDao, commentDao, listingDao, userDao);
        this.conversationService = new ConversationServiceImpl(dataSource, conversationDao, messageDao, listingDao, userDao);
        this.adminService = new AdminServiceImpl(dataSource, userDao, listingDao, commentDao);

        // 4) 初始化 Controller：负责 HTTP 层的参数解析/校验与调用 Service，并输出统一 JSON。
        this.authController = new AuthController(authService);
        this.userController = new UserController(userService, listingService, socialService);
        this.listingController = new ListingController(listingService);
        this.socialController = new SocialController(socialService);
        this.conversationController = new ConversationController(conversationService);
        this.adminController = new AdminController(adminService);
    }

    /**
     * 获取应用上下文单例。
     *
     * @return 全局唯一的 {@link AppContext}
     */
    public static AppContext getInstance() {
        return INSTANCE;
    }

    /**
     * 获取认证相关 Controller。
     *
     * @return {@link AuthController}
     */
    public AuthController getAuthController() {
        return authController;
    }

    /**
     * 获取用户相关 Controller。
     *
     * @return {@link UserController}
     */
    public UserController getUserController() {
        return userController;
    }

    /**
     * 获取商品/闲置相关 Controller。
     *
     * @return {@link ListingController}
     */
    public ListingController getListingController() {
        return listingController;
    }

    /**
     * 获取社交相关（关注/收藏/评论）Controller。
     *
     * @return {@link SocialController}
     */
    public SocialController getSocialController() {
        return socialController;
    }

    /**
     * 获取会话/消息相关 Controller。
     *
     * @return {@link ConversationController}
     */
    public ConversationController getConversationController() {
        return conversationController;
    }

    /**
     * 获取管理端 Controller。
     *
     * @return {@link AdminController}
     */
    public AdminController getAdminController() {
        return adminController;
    }
}

