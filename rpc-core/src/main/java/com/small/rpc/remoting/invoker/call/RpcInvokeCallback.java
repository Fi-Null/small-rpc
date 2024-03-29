package com.small.rpc.remoting.invoker.call;

/**
 * @ClassName RpcInvokeCallback
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 00:18
 * @Version 1.0
 **/
public abstract class RpcInvokeCallback<T> {

    public abstract void onSuccess(T result);

    public abstract void onFailure(Throwable exception);


    // ---------------------- thread invoke callback ----------------------

    private static ThreadLocal<RpcInvokeCallback> threadInvokerCallBack = new ThreadLocal<>();

    /**
     * get callback
     *
     * @return
     */
    public static RpcInvokeCallback getCallback() {
        RpcInvokeCallback invokeCallback = threadInvokerCallBack.get();
        threadInvokerCallBack.remove();
        return invokeCallback;
    }

    /**
     * set future
     *
     * @param invokeCallback
     */
    public static void setCallback(RpcInvokeCallback invokeCallback) {
        threadInvokerCallBack.set(invokeCallback);
    }

    /**
     * remove future
     */
    public static void removeCallback() {
        threadInvokerCallBack.remove();
    }
}
