package com.small.rpc.net;

import com.small.rpc.net.param.RpcRequest;
import com.small.rpc.remoting.invoker.reference.RpcReferenceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName Client
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 00:03
 * @Version 1.0
 **/
public abstract class Client {

    protected static final Logger logger = LoggerFactory.getLogger(Client.class);


    // ---------------------- init ----------------------

    protected volatile RpcReferenceBean rpcReferenceBean;

    public void init(RpcReferenceBean rpcReferenceBean) {
        this.rpcReferenceBean = rpcReferenceBean;
    }


    // ---------------------- send ----------------------

    /**
     * async send, bind requestId and future-response
     *
     * @param address
     * @param rpcRequest
     * @return
     * @throws Exception
     */
    public abstract void asyncSend(String address, RpcRequest rpcRequest) throws Exception;
}

