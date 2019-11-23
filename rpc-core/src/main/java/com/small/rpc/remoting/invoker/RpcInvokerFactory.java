package com.small.rpc.remoting.invoker;

import com.small.rpc.net.param.BaseCallback;
import com.small.rpc.net.param.RpcFutureResponse;
import com.small.rpc.net.param.RpcResponse;
import com.small.rpc.util.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @ClassName RpcInvokerFactory
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 00:17
 * @Version 1.0
 **/
public class RpcInvokerFactory {

    private static Logger logger = LoggerFactory.getLogger(RpcInvokerFactory.class);


    // ---------------------- service registry ----------------------

    private List<BaseCallback> stopCallbackList = new ArrayList<>();

    public void addStopCallBack(BaseCallback callback) {
        stopCallbackList.add(callback);
    }


    // ---------------------- future-response pool ----------------------

    // RpcFutureResponseFactory

    private ConcurrentMap<String, RpcFutureResponse> futureResponsePool = new ConcurrentHashMap<String, RpcFutureResponse>();

    public void setInvokerFuture(String requestId, RpcFutureResponse futureResponse) {
        futureResponsePool.put(requestId, futureResponse);
    }

    public void removeInvokerFuture(String requestId) {
        futureResponsePool.remove(requestId);
    }

    public void notifyInvokerFuture(String requestId, final RpcResponse RpcResponse) {

        // get
        final RpcFutureResponse futureResponse = futureResponsePool.get(requestId);
        if (futureResponse == null) {
            return;
        }

        // notify
        if (futureResponse.getInvokeCallback() != null) {

            // callback type
            try {
                executeResponseCallback(() -> {
                    if (RpcResponse.getErrorMsg() != null) {
                        futureResponse.getInvokeCallback().onFailure(new RpcException(RpcResponse.getErrorMsg()));
                    } else {
                        futureResponse.getInvokeCallback().onSuccess(RpcResponse.getResult());
                    }
                });
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {

            // other nomal type
            futureResponse.setResponse(RpcResponse);
        }

        // do remove
        futureResponsePool.remove(requestId);

    }

    // ---------------------- response callback ThreadPool ----------------------

    private ThreadPoolExecutor responseCallbackThreadPool = null;

    public void executeResponseCallback(Runnable runnable) {

        if (responseCallbackThreadPool == null) {
            synchronized (this) {
                if (responseCallbackThreadPool == null) {
                    responseCallbackThreadPool = new ThreadPoolExecutor(
                            10,
                            100,
                            60L,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(1000),
                            r -> new Thread(r, "small-rpc, XxlRpcInvokerFactory-responseCallbackThreadPool-" + r.hashCode()),
                            (r, executor) -> {
                                throw new RpcException("small-rpc Invoke Callback Thread pool is EXHAUSTED!");
                            });        // default maxThreads 300, minThreads 60
                }
            }
        }
        responseCallbackThreadPool.execute(runnable);
    }


}
