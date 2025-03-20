package com.quicksand.bigdata.query.utils;

import com.quicksand.bigdata.vars.concurrents.TraceableThreadPoolTaskExecutor;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedRunnable;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionException;

/**
 * com.quicksand.bigdata.engine.query.utils
 * QueryFuture
 * <p>
 *
 * @author xupei
 * @date 2021/7/27
 */
public interface TraceFuture {

    /**
     * 弹性收缩池子
     * (非实查路径)
     */

    ThreadPoolTaskExecutor threadPoolTaskExecutor = TraceableThreadPoolTaskExecutor.buildThreadPoolTaskExecutor(0, 256, 30, 0, "TRAC-", (r, executor) -> {
        throw new RejectedExecutionException();
    });

    static Future<Void> run(CheckedRunnable unit) {
        return Future.run(threadPoolTaskExecutor, () -> Try.run(unit).andFinally(MDC::clear));
    }

    static <T> Future<T> of(CheckedFunction0<? extends T> computation) {
        return Future.of(threadPoolTaskExecutor, () -> Try.of(computation).andFinally(MDC::clear).get());
    }

}
