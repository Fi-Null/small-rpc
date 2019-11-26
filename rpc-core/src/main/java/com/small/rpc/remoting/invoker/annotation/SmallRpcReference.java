package com.small.rpc.remoting.invoker.annotation;

import com.small.rpc.remoting.invoker.call.CallType;
import com.small.rpc.remoting.invoker.route.LoadBalance;
import com.small.rpc.remoting.net.Client;
import com.small.rpc.remoting.net.netty.client.NettyClient;
import com.small.rpc.serialize.Serializer;
import com.small.rpc.serialize.hessian.HessianSerializer;

public @interface SmallRpcReference {

    Class<? extends Client> client() default NettyClient.class;

    Class<? extends Serializer> serializer() default HessianSerializer.class;

    CallType callType() default CallType.SYNC;

    LoadBalance loadBalance() default LoadBalance.ROUND;

    //Class<?> iface;
    String version() default "";

    long timeout() default 1000;

    String address() default "";

    String accessToken() default "";

}
