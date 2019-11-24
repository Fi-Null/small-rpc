package com.small.rpc.remoting.invoker.reference;

import com.small.rpc.remoting.invoker.RpcInvokerFactory;
import com.small.rpc.remoting.invoker.call.CallType;
import com.small.rpc.remoting.invoker.call.RpcInvokeCallback;
import com.small.rpc.remoting.invoker.route.LoadBalance;
import com.small.rpc.remoting.net.Client;
import com.small.rpc.remoting.net.netty.client.NettyClient;
import com.small.rpc.serialize.Serializer;
import com.small.rpc.serialize.hessian.HessianSerializer;
import com.small.rpc.util.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName RpcReferenceBean
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 00:07
 * @Version 1.0
 **/
public class RpcReferenceBean {

    private static final Logger logger = LoggerFactory.getLogger(RpcReferenceBean.class);
    // [tips01: save 30ms/100invoke. why why why??? with this logger, it can save lots of time.]


    // ---------------------- config ----------------------

    private Class<? extends Client> client = NettyClient.class;
    private Class<? extends Serializer> serializer = HessianSerializer.class;
    private CallType callType = CallType.SYNC;
    private LoadBalance loadBalance = LoadBalance.RANDOM;

    private Class<?> iface = null;
    private String version = null;

    private long timeout = 1000;

    private String address = null;
    private String accessToken = null;

    private RpcInvokeCallback invokeCallback = null;

    private RpcInvokerFactory invokerFactory = null;


    // set
    public void setClient(Class<? extends Client> client) {
        this.client = client;
    }

    public void setSerializer(Class<? extends Serializer> serializer) {
        this.serializer = serializer;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    public void setIface(Class<?> iface) {
        this.iface = iface;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setInvokeCallback(RpcInvokeCallback invokeCallback) {
        this.invokeCallback = invokeCallback;
    }

    public void setInvokerFactory(RpcInvokerFactory invokerFactory) {
        this.invokerFactory = invokerFactory;
    }


    // get
    public Serializer getSerializerInstance() {
        return serializerInstance;
    }

    public long getTimeout() {
        return timeout;
    }

    public RpcInvokerFactory getInvokerFactory() {
        return invokerFactory;
    }

    public Class<?> getIface() {
        return iface;
    }


    // ---------------------- initClient ----------------------

    private Client clientInstance = null;
    private Serializer serializerInstance = null;

    public RpcReferenceBean initClient() throws Exception {

        // valid
        if (this.client == null) {
            throw new RpcException("small-rpc reference client missing.");
        }
        if (this.serializer == null) {
            throw new RpcException("small-rpc reference serializer missing.");
        }
        if (this.callType == null) {
            throw new RpcException("small-rpc reference callType missing.");
        }
        if (this.loadBalance == null) {
            throw new RpcException("small-rpc reference loadBalance missing.");
        }
        if (this.iface == null) {
            throw new RpcException("small-rpc reference iface missing.");
        }
        if (this.timeout < 0) {
            this.timeout = 0;
        }
        if (this.invokerFactory == null) {
            //this.invokerFactory = RpcInvokerFactory.getInstance();
        }

        // init serializerInstance
        this.serializerInstance = serializer.newInstance();

        // init Client
        clientInstance = client.newInstance();
        clientInstance.init(this);

        return this;
    }


    // ---------------------- util ----------------------

//    public Object getObject() throws Exception {
//
//        // initClient
//        initClient();
//
//        // newProxyInstance
//        return Proxy.newProxyInstance(Thread.currentThread()
//                        .getContextClassLoader(), new Class[] { iface },
//                new InvocationHandler() {
//                    @Override
//                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//
//                        // method param
//                        String className = method.getDeclaringClass().getName();	// iface.getName()
//                        String varsion_ = version;
//                        String methodName = method.getName();
//                        Class<?>[] parameterTypes = method.getParameterTypes();
//                        Object[] parameters = args;
//
//                        // filter for generic
//                        if (className.equals(RpcGenericService.class.getName()) && methodName.equals("invoke")) {
//
//                            Class<?>[] paramTypes = null;
//                            if (args[3]!=null) {
//                                String[] paramTypes_str = (String[]) args[3];
//                                if (paramTypes_str.length > 0) {
//                                    paramTypes = new Class[paramTypes_str.length];
//                                    for (int i = 0; i < paramTypes_str.length; i++) {
//                                        paramTypes[i] = ClassUtil.resolveClass(paramTypes_str[i]);
//                                    }
//                                }
//                            }
//
//                            className = (String) args[0];
//                            varsion_ = (String) args[1];
//                            methodName = (String) args[2];
//                            parameterTypes = paramTypes;
//                            parameters = (Object[]) args[4];
//                        }
//
//                        // filter method like "Object.toString()"
//                        if (className.equals(Object.class.getName())) {
//                            logger.info(">>>>>>>>>>> small-rpc proxy class-method not support [{}#{}]", className, methodName);
//                            throw new RpcException("small-rpc proxy class-method not support");
//                        }
//
//                        // address
//                        String finalAddress = address;
//                        if (finalAddress==null || finalAddress.trim().length()==0) {
//                            if (invokerFactory!=null && invokerFactory.getServiceRegistry()!=null) {
//                                // discovery
//                                String serviceKey = RpcProviderFactory.makeServiceKey(className, varsion_);
//                                TreeSet<String> addressSet = invokerFactory.getServiceRegistry().discovery(serviceKey);
//                                // load balance
//                                if (addressSet==null || addressSet.size()==0) {
//                                    // pass
//                                } else if (addressSet.size()==1) {
//                                    finalAddress = addressSet.first();
//                                } else {
//                                    finalAddress = loadBalance.RpcInvokerRouter.route(serviceKey, addressSet);
//                                }
//
//                            }
//                        }
//                        if (finalAddress==null || finalAddress.trim().length()==0) {
//                            throw new RpcException("small-rpc reference bean["+ className +"] address empty");
//                        }
//
//                        // request
//                        RpcRequest RpcRequest = new RpcRequest();
//                        RpcRequest.setRequestId(UUID.randomUUID().toString());
//                        RpcRequest.setCreateMillisTime(System.currentTimeMillis());
//                        RpcRequest.setAccessToken(accessToken);
//                        RpcRequest.setClassName(className);
//                        RpcRequest.setMethodName(methodName);
//                        RpcRequest.setParameterTypes(parameterTypes);
//                        RpcRequest.setParameters(parameters);
//                        RpcRequest.setVersion(version);
//
//                        // send
//                        if (CallType.SYNC == callType) {
//                            // future-response set
//                            RpcFutureResponse futureResponse = new RpcFutureResponse(invokerFactory, RpcRequest, null);
//                            try {
//                                // do invoke
//                                clientInstance.asyncSend(finalAddress, RpcRequest);
//
//                                // future get
//                                RpcResponse RpcResponse = futureResponse.get(timeout, TimeUnit.MILLISECONDS);
//                                if (RpcResponse.getErrorMsg() != null) {
//                                    throw new RpcException(RpcResponse.getErrorMsg());
//                                }
//                                return RpcResponse.getResult();
//                            } catch (Exception e) {
//                                logger.info(">>>>>>>>>>> small-rpc, invoke error, address:{}, RpcRequest{}", finalAddress, RpcRequest);
//
//                                throw (e instanceof RpcException)?e:new RpcException(e);
//                            } finally{
//                                // future-response remove
//                                futureResponse.removeInvokerFuture();
//                            }
//                        } else if (CallType.FUTURE == callType) {
//                            // future-response set
//                            RpcFutureResponse futureResponse = new RpcFutureResponse(invokerFactory, RpcRequest, null);
//                            try {
//                                // invoke future set
//                                RpcInvokeFuture invokeFuture = new RpcInvokeFuture(futureResponse);
//                                RpcInvokeFuture.setFuture(invokeFuture);
//
//                                // do invoke
//                                clientInstance.asyncSend(finalAddress, RpcRequest);
//
//                                return null;
//                            } catch (Exception e) {
//                                logger.info(">>>>>>>>>>> small-rpc, invoke error, address:{}, RpcRequest{}", finalAddress, RpcRequest);
//
//                                // future-response remove
//                                futureResponse.removeInvokerFuture();
//
//                                throw (e instanceof RpcException)?e:new RpcException(e);
//                            }
//
//                        } else if (CallType.CALLBACK == callType) {
//
//                            // get callback
//                            RpcInvokeCallback finalInvokeCallback = invokeCallback;
//                            RpcInvokeCallback threadInvokeCallback = RpcInvokeCallback.getCallback();
//                            if (threadInvokeCallback != null) {
//                                finalInvokeCallback = threadInvokeCallback;
//                            }
//                            if (finalInvokeCallback == null) {
//                                throw new RpcException("small-rpc RpcInvokeCallback（CallType="+ CallType.CALLBACK.name() +"） cannot be null.");
//                            }
//
//                            // future-response set
//                            RpcFutureResponse futureResponse = new RpcFutureResponse(invokerFactory, RpcRequest, finalInvokeCallback);
//                            try {
//                                clientInstance.asyncSend(finalAddress, RpcRequest);
//                            } catch (Exception e) {
//                                logger.info(">>>>>>>>>>> small-rpc, invoke error, address:{}, RpcRequest{}", finalAddress, RpcRequest);
//
//                                // future-response remove
//                                futureResponse.removeInvokerFuture();
//
//                                throw (e instanceof RpcException)?e:new RpcException(e);
//                            }
//
//                            return null;
//                        } else if (CallType.ONEWAY == callType) {
//                            clientInstance.asyncSend(finalAddress, RpcRequest);
//                            return null;
//                        } else {
//                            throw new RpcException("small-rpc callType["+ callType +"] invalid");
//                        }
//
//                    }
//                });
//    }

}
