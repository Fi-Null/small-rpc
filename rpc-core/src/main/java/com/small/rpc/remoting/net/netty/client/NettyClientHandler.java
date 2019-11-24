package com.small.rpc.remoting.net.netty.client;

import com.small.rpc.remoting.invoker.RpcInvokerFactory;
import com.small.rpc.remoting.net.param.HeartBeat;
import com.small.rpc.remoting.net.param.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName NettyClientHandler
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 00:51
 * @Version 1.0
 **/
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);


    private RpcInvokerFactory rpcInvokerFactory;
    private NettyConnectClient nettyConnectClient;

    public NettyClientHandler(final RpcInvokerFactory RpcInvokerFactory, NettyConnectClient nettyConnectClient) {
        this.rpcInvokerFactory = RpcInvokerFactory;
        this.nettyConnectClient = nettyConnectClient;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse RpcResponse) throws Exception {

        // notify response
        rpcInvokerFactory.notifyInvokerFuture(RpcResponse.getRequestId(), RpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(">>>>>>>>>>> small-rpc netty client caught exception", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            nettyConnectClient.send(HeartBeat.BEAT_PING);    // beat N, close if fail(may throw error)
            logger.debug(">>>>>>>>>>> small-rpc netty client send beat-ping.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}