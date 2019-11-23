package com.small.rpc.remoting.route;

/**
 * @ClassName LoadBalance
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 00:28
 * @Version 1.0
 **/
public enum LoadBalance {
    RANDOM(new RpcLoadBalanceRandomStrategy());
//    ROUND(new RpcLoadBalanceRoundStrategy()),
//    LRU(new RpcLoadBalanceLRUStrategy()),
//    LFU(new RpcLoadBalanceLFUStrategy()),
//    CONSISTENT_HASH(new RpcLoadBalanceConsistentHashStrategy());


    public final RpcLoadBalance rpcInvokerRouter;

    private LoadBalance(RpcLoadBalance rpcInvokerRouter) {
        this.rpcInvokerRouter = rpcInvokerRouter;
    }


    public static LoadBalance match(String name, LoadBalance defaultRouter) {
        for (LoadBalance item : LoadBalance.values()) {
            if (item.equals(name)) {
                return item;
            }
        }
        return defaultRouter;
    }
}
