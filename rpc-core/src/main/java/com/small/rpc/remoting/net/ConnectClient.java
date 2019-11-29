package com.small.rpc.remoting.net;

import com.small.rpc.remoting.invoker.RpcInvokerFactory;
import com.small.rpc.remoting.invoker.reference.RpcReferenceBean;
import com.small.rpc.remoting.net.param.RpcRequest;
import com.small.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName ConnectClient
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 00:34
 * @Version 1.0
 **/
public abstract class ConnectClient {

    protected static transient Logger logger = LoggerFactory.getLogger(ConnectClient.class);

    // ---------------------- iface ----------------------

    public abstract void init(String address, final Serializer serializer, final RpcInvokerFactory rpcInvokerFactory) throws Exception;

    public abstract void close();

    public abstract boolean isValidate();

    public abstract void send(RpcRequest rpcRequest) throws Exception;


    // ---------------------- client pool map ----------------------

    /**
     * async send
     */
    public static void asyncSend(RpcRequest rpcRequest, String address,
                                 Class<? extends ConnectClient> connectClientImpl,
                                 final RpcReferenceBean rpcReferenceBean) throws Exception {

        // client pool	[tips03 : may save 35ms/100invoke if move it to constructor, but it is necessary. cause by ConcurrentHashMap.get]
        ConnectClient clientPool = ConnectClientHolder.getPool(address, connectClientImpl, rpcReferenceBean);

        try {
            // do invoke
            clientPool.send(rpcRequest);
        } catch (Exception e) {
            throw e;
        }

    }
}
