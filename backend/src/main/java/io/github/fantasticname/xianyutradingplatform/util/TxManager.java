package io.github.fantasticname.xianyutradingplatform.util;

import io.github.fantasticname.xianyutradingplatform.common.ErrorCode;
import io.github.fantasticname.xianyutradingplatform.exception.BusinessException;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 我自定义的事务管理类
 * @author FantasticName
 */
/**
 * 轻量级事务管理器：为同一线程内的一组 DAO 操作提供一致的事务边界。
 *
 * <p>核心思路是把当前事务连接绑定到 {@link ThreadLocal}，让 DAO 在“有事务上下文”时复用同一连接；
 * 在“无事务上下文”时则由 DAO 自己获取连接并独立关闭。</p>
 *
 * <p>该实现用于替代框架中的声明式事务：显式地在 Service 层包裹事务，并让底层 DAO 感知并复用连接。</p>
 *
 * @implNote
 * - 不支持真正的嵌套事务：当检测到已有连接时，会直接复用并执行 supplier，不会开启新事务。
 * - 异常策略：业务异常透传；非业务异常统一转换为 {@link BusinessException} 并回滚。
 */
public final class TxManager {
    // 1. 定义一个 ThreadLocal，专门用来装 Connection。
    // 这里把名字起成 CTX（Context 上下文），意思是"当前线程的事务上下文"。
    // CTX有一个set(Connection conn)方法，把connection对象绑到当前线程上,这个方法在 executeInTransaction 方法中调用
    // CTX有一个get()方法,用于获取: 与"当前线程上下文"绑定在一起的"connection对象"
    private static final ThreadLocal<Connection> CTX = new ThreadLocal<>();


    // 2. 私有构造器，不让外面 new。全是静态方法，当工具类用。
    private TxManager() {
    }

    /**
     * 获取当前线程绑定的事务连接。
     *
     * <p>DAO 层可通过该方法判断是否处于 {@link #executeInTransaction(DataSource, TxSupplier)} 事务范围内：</p>
     * - 返回非空：表示当前线程已有事务连接，应复用该连接执行 SQL；连接的提交/回滚由事务管理器负责。<br>
     * - 返回空：表示当前线程没有事务上下文，DAO 需要自行从 {@link DataSource} 获取连接并在本次调用结束后关闭。
     *
     * @return 当前线程绑定的连接；若不在事务中则返回 {@code null}
     */
    public static Connection currentConnection() {
        return CTX.get();
    }

    /**
     * 在一个数据库事务中执行给定的业务逻辑，并确保提交/回滚与资源清理。
     *
     * <p>执行流程：</p>
     * 1) 如果当前线程已经有事务连接（外层事务），则直接复用该事务并执行 supplier（避免开启“嵌套事务”）。<br>
     * 2) 否则从数据源获取连接、关闭自动提交，并将连接绑定到线程上下文。<br>
     * 3) 执行业务逻辑：成功则提交；发生业务异常则回滚并原样抛出；其他异常回滚并转换为统一的业务异常。<br>
     * 4) finally 中移除线程上下文，并尽力恢复连接的自动提交开关；连接由 try-with-resources 关闭。
     *
     * @param ds 数据库连接池对象，如果当前线程没有事务，用它开连接connection。
     * @param supplier 事务内要执行的逻辑（通常由 Service 层提供）
     * @param <T> 返回值类型
     * @return supplier 的执行结果
     * @throws BusinessException 业务异常：包括 supplier 主动抛出的业务异常，以及系统异常被统一包装后的业务异常
     */
    public static <T> T executeInTransaction(DataSource ds, TxSupplier<T> supplier) {
        Connection existing = CTX.get();
        if (existing != null) {
            
            // 已经有连接了？说明外层已经调过 executeInTransaction 了。
            // 这里我们采取简单策略：直接执行业务逻辑，不开启新连接，不提交，不关闭。
            return supplier.get();
        }


        // try-with-resources：不管怎么着，出了这个代码块，conn 一定会被 close()。
        // 数据库连接池有装饰器,所以close()是归还而不是销毁
        try (Connection conn = ds.getConnection()) {
            // 以“手动提交”的方式开启事务，并将连接绑定到当前线程，供 DAO 复用。
            conn.setAutoCommit(false);
            CTX.set(conn);
            try {
                // 执行事务内逻辑：若无异常则提交并返回结果。
                T result = supplier.get();
                conn.commit();
                return result;
            } catch (BusinessException e) {
                // 业务异常：需要回滚，但异常语义保持不变，直接透传给上层。
                conn.rollback();
                throw e;
            } catch (Exception e) {
                // 非业务异常：回滚并统一转换为“内部错误”，避免向外暴露底层实现细节。
                conn.rollback();
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "事务执行失败");
            } finally {
                // 特别特别重要!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // 因为Jetty/Tomcat 线程池的原因, 请求结束后线程不会死, 但是: 一个请求对应一个连接,
                // 清理线程上下文，避免连接泄漏到后续请求；并尽力恢复自动提交状态，便于连接回收到池中时可复用。
                CTX.remove();
                conn.setAutoCommit(true);
            }
        } catch (BusinessException e) {
            // 内部已经构造好的业务异常（包含 supplier 透传或统一包装）直接向外抛出。
            throw e;
        } catch (Exception e) {
            // 获取连接/配置事务失败等：统一转换为“数据库连接失败”。
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "数据库连接失败");
        }
    }
}

