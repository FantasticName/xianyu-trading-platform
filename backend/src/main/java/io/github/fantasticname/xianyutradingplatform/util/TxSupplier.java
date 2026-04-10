package io.github.fantasticname.xianyutradingplatform.util;

/**
 * 事务回调接口：封装需要在 {@link TxManager#executeInTransaction(javax.sql.DataSource, TxSupplier)} 中执行的逻辑。
 *
 * <p>之所以使用自定义函数式接口而不是直接使用 {@code java.util.function.Supplier}：
 * 1. 见名知意：看到 TxSupplier，一眼就知道："哦，这段代码是要在事务里跑的，我不能在里面写太耗时的 IO 操作（比如发邮件），不然锁表时间太长。" . 
 * 看到 Supplier，就不知道有没有事务。<br>
 * 2. 预留扩展口：万一以后你想让事务里能拿到当前登录用户 ID，直接在 get() 里加个参数就行。用 Java 自带的接口，参数个数一变就没办法兼容了。</p>
 *
 * @param <T> 回调执行结果类型
 *
 * @author FantasticName
 */
@FunctionalInterface
public interface TxSupplier<T> {
    /**
     * 获取事务内执行结果。
     *
     * <p>实现方通常在方法内部串联多个 DAO 调用；事务提交/回滚由 {@link TxManager} 负责。</p>
     *
     * @return 执行结果
     * @throws RuntimeException 允许抛出运行时异常；事务管理器会将非业务异常统一包装为业务异常
     */
    T get();
}

