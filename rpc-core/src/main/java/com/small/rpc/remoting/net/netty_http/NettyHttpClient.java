package com.small.rpc.remoting.net.netty_http;

import com.small.rpc.remoting.net.Client;
import com.small.rpc.remoting.net.ConnectClient;
import com.small.rpc.remoting.net.param.RpcRequest;

/**
 * @ClassName NettyHttpClient
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 17:40
 * @Version 1.0
 **/
public class NettyHttpClient extends Client {

    private Class<? extends ConnectClient> connectClientImpl = NettyHttpConnectClient.class;

    @Override
    public void asyncSend(String address, RpcRequest rpcRequest) throws Exception {
        ConnectClient.asyncSend(rpcRequest, address, connectClientImpl, rpcReferenceBean);
    }
}
