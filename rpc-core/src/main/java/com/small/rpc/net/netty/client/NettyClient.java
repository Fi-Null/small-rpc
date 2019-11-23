package com.small.rpc.net.netty.client;

import com.small.rpc.net.Client;
import com.small.rpc.net.ConnectClient;
import com.small.rpc.net.param.RpcRequest;

/**
 * @ClassName NettyClient
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 00:10
 * @Version 1.0
 **/
public class NettyClient extends Client {

    private Class<? extends ConnectClient> connectClientImpl = NettyConnectClient.class;

    @Override
    public void asyncSend(String address, RpcRequest rpcRequest) throws Exception {
        ConnectClient.asyncSend(rpcRequest, address, connectClientImpl, rpcReferenceBean);
    }
}
