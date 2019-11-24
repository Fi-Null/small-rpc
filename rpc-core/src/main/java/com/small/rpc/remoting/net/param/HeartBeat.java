package com.small.rpc.remoting.net.param;

/**
 * @ClassName HeartBeat
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 00:39
 * @Version 1.0
 **/
public final class HeartBeat {

    public static final int BEAT_INTERVAL = 30;
    public static final String BEAT_ID = "BEAT_PING_PONG";

    public static RpcRequest BEAT_PING;

    static {
        BEAT_PING = new RpcRequest();
        BEAT_PING.setRequestId(BEAT_ID);
    }
}
