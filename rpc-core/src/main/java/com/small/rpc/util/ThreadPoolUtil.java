package com.small.rpc.util;

import java.util.concurrent.*;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/22/19 6:05 PM
 */
public class ThreadPoolUtil {

    /**
     * make server thread pool
     *
     * @param serverType
     * @return
     */
    public static ThreadPoolExecutor makeServerThreadPool(final String serverType, int corePoolSize, int maxPoolSize) {
        ThreadPoolExecutor serverHandlerPool = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "small-rpc, " + serverType + "-serverHandlerPool-" + r.hashCode());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        throw new RpcException("small-rpc " + serverType + " Thread pool is EXHAUSTED!");
                    }
                });        // default maxThreads 300, minThreads 60

        return serverHandlerPool;
    }

}
