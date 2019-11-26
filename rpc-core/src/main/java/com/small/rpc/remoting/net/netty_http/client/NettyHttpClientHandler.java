package com.small.rpc.remoting.net.netty_http.client;

import com.small.rpc.remoting.invoker.RpcInvokerFactory;
import com.small.rpc.remoting.net.param.HeartBeat;
import com.small.rpc.remoting.net.param.RpcResponse;
import com.small.rpc.serialize.Serializer;
import com.small.rpc.util.RpcException;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName NettyHttpClientHandler
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 17:46
 * @Version 1.0
 **/
public class NettyHttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private static final Logger logger = LoggerFactory.getLogger(NettyHttpClientHandler.class);


    private RpcInvokerFactory rpcInvokerFactory;
    private Serializer serializer;
    private NettyHttpConnectClient nettyHttpConnectClient;

    public NettyHttpClientHandler(final RpcInvokerFactory rpcInvokerFactory, Serializer serializer, final NettyHttpConnectClient nettyHttpConnectClient) {
        this.rpcInvokerFactory = rpcInvokerFactory;
        this.serializer = serializer;
        this.nettyHttpConnectClient = nettyHttpConnectClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {

        // valid status
        if (!HttpResponseStatus.OK.equals(msg.status())) {
            throw new RpcException("small-rpc response status invalid.");
        }

        // response parse
        byte[] responseBytes = ByteBufUtil.getBytes(msg.content());

        // valid length
        if (responseBytes.length == 0) {
            throw new RpcException("small-rpc response data empty.");
        }

        // response deserialize
        RpcResponse rpcResponse = (RpcResponse) serializer.deserialize(responseBytes, RpcResponse.class);

        // notify response
        rpcInvokerFactory.notifyInvokerFuture(rpcResponse.getRequestId(), rpcResponse);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(">>>>>>>>>>> small-rpc netty_http client caught exception", cause);
        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            nettyHttpConnectClient.send(HeartBeat.BEAT_PING);    // beat N, close if fail(may throw error)
            logger.debug(">>>>>>>>>>> small-rpc netty_http client send beat-ping.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
