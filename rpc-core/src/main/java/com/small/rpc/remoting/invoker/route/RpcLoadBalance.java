package com.small.rpc.remoting.invoker.route;

import java.util.TreeSet;

/**
 * @InterfaceName RpcLoadBalance
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 00:30
 * @Version 1.0
 **/
public interface RpcLoadBalance {

    String route(String serviceKey, TreeSet<String> addressSet);

}
