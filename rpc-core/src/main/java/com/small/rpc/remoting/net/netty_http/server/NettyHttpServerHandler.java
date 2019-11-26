package com.small.rpc.remoting.net.netty_http.server;

import com.small.rpc.remoting.net.param.HeartBeat;
import com.small.rpc.remoting.net.param.RpcRequest;
import com.small.rpc.remoting.net.param.RpcResponse;
import com.small.rpc.remoting.provider.RpcProviderFactory;
import com.small.rpc.util.RpcException;
import com.small.rpc.util.ThrowableUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/26/19 6:07 PM
 */
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyHttpServerHandler.class);


    private RpcProviderFactory rpcProviderFactory;
    private ThreadPoolExecutor serverHandlerPool;

    public NettyHttpServerHandler(final RpcProviderFactory rpcProviderFactory, final ThreadPoolExecutor serverHandlerPool) {
        this.rpcProviderFactory = rpcProviderFactory;
        this.serverHandlerPool = serverHandlerPool;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        // request parse
        final byte[] requestBytes = ByteBufUtil.getBytes(msg.content());    // byteBuf.toString(io.netty.util.CharsetUtil.UTF_8);
        final String uri = msg.uri();
        final boolean keepAlive = HttpUtil.isKeepAlive(msg);

        // do invoke
        serverHandlerPool.execute(() -> process(ctx, uri, requestBytes, keepAlive));
    }

    private void process(ChannelHandlerContext ctx, String uri, byte[] requestBytes, boolean keepAlive) {
        String requestId = null;
        try {
            if ("/services".equals(uri)) {    // services mapping

                // request
                StringBuffer stringBuffer = new StringBuffer("<ui>");
                for (String serviceKey : rpcProviderFactory.getServiceData().keySet()) {
                    stringBuffer.append("<li>").append(serviceKey).append(": ").append(rpcProviderFactory.getServiceData().get(serviceKey)).append("</li>");
                }
                stringBuffer.append("</ui>");

                // response serialize
                byte[] responseBytes = stringBuffer.toString().getBytes("UTF-8");

                // response-write
                writeResponse(ctx, keepAlive, responseBytes);

            } else {

                // valid
                if (requestBytes.length == 0) {
                    throw new RpcException("small-rpc request data empty.");
                }

                // request deserialize
                RpcRequest rpcRequest = (RpcRequest) rpcProviderFactory.getSerializerInstance().deserialize(requestBytes, RpcRequest.class);
                requestId = rpcRequest.getRequestId();

                // filter beat
                if (HeartBeat.BEAT_ID.equalsIgnoreCase(rpcRequest.getRequestId())) {
                    logger.debug(">>>>>>>>>>> small-rpc provider netty_http server read beat-ping.");
                    return;
                }

                // invoke + response
                RpcResponse RpcResponse = rpcProviderFactory.invokeService(rpcRequest);

                // response serialize
                byte[] responseBytes = rpcProviderFactory.getSerializerInstance().serialize(RpcResponse);

                // response-write
                writeResponse(ctx, keepAlive, responseBytes);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            // response error
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setRequestId(requestId);
            rpcResponse.setErrorMsg(ThrowableUtil.toString(e));

            // response serialize
            byte[] responseBytes = rpcProviderFactory.getSerializerInstance().serialize(rpcResponse);

            // response-write
            writeResponse(ctx, keepAlive, responseBytes);
        }

    }

    /**
     * write response
     */
    private void writeResponse(ChannelHandlerContext ctx, boolean keepAlive, byte[] responseBytes) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBytes));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");       // HttpHeaderValues.TEXT_PLAIN.toString()
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        if (keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(">>>>>>>>>>> small-rpc provider netty_http server caught exception", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();      // beat 3N, close if idle
            logger.debug(">>>>>>>>>>> small-rpc provider netty_http server close an idle channel.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}