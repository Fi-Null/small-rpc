package com.small.rpc.remoting.net;

import com.small.rpc.remoting.invoker.reference.RpcReferenceBean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @ClassName ConnectClientHolder
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 14:56
 * @Version 1.0
 **/
public class ConnectClientHolder {

    private static volatile ConcurrentMap<String, ConnectClient> connectClientMap;        // (static) alread addStopCallBack
    private static volatile ConcurrentMap<String, Object> connectClientLockMap = new ConcurrentHashMap<>();


    public static ConnectClient getPool(String address, Class<? extends ConnectClient> connectClientImpl,
                                        final RpcReferenceBean RpcReferenceBean) throws Exception {

        // init base compont, avoid repeat init
        if (connectClientMap == null) {
            synchronized (ConnectClient.class) {
                if (connectClientMap == null) {
                    // init
                    connectClientMap = new ConcurrentHashMap<>();
                    // stop callback
                    RpcReferenceBean.getInvokerFactory().addStopCallBack(() -> {
                        if (connectClientMap.size() > 0) {
                            for (String key : connectClientMap.keySet()) {
                                ConnectClient clientPool = connectClientMap.get(key);
                                clientPool.close();
                            }
                            connectClientMap.clear();
                        }
                    });
                }
            }
        }

        // get-valid client
        ConnectClient connectClient = connectClientMap.get(address);
        if (connectClient != null && connectClient.isValidate()) {
            return connectClient;
        }

        // lock
        Object clientLock = connectClientLockMap.get(address);
        if (clientLock == null) {
            connectClientLockMap.putIfAbsent(address, new Object());
            clientLock = connectClientLockMap.get(address);
        }

        // remove-create new client
        synchronized (clientLock) {

            // get-valid client, avlid repeat
            connectClient = connectClientMap.get(address);
            if (connectClient != null && connectClient.isValidate()) {
                return connectClient;
            }

            // remove old
            if (connectClient != null) {
                connectClient.close();
                connectClientMap.remove(address);
            }

            // set pool
            ConnectClient connectClient_new = connectClientImpl.newInstance();
            try {
                connectClient_new.init(address, RpcReferenceBean.getSerializerInstance(), RpcReferenceBean.getInvokerFactory());
                connectClientMap.put(address, connectClient_new);
            } catch (Exception e) {
                connectClient_new.close();
                throw e;
            }

            return connectClient_new;
        }


    }
}
