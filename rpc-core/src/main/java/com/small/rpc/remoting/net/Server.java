package com.small.rpc.remoting.net;

import com.small.rpc.remoting.net.param.BaseCallback;
import com.small.rpc.remoting.provider.RpcProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName Server
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 17:59
 * @Version 1.0
 **/
public abstract class Server {
    protected static final Logger logger = LoggerFactory.getLogger(Server.class);


    private BaseCallback startedCallback;
    private BaseCallback stopedCallback;

    public void setStartedCallback(BaseCallback startedCallback) {
        this.startedCallback = startedCallback;
    }

    public void setStopedCallback(BaseCallback stopedCallback) {
        this.stopedCallback = stopedCallback;
    }


    /**
     * start server
     *
     * @param rpcProviderFactory
     * @throws Exception
     */
    public abstract void start(final RpcProviderFactory rpcProviderFactory) throws Exception;

    /**
     * callback when started
     */
    public void onStarted() {
        if (startedCallback != null) {
            try {
                startedCallback.run();
            } catch (Exception e) {
                logger.error(">>>>>>>>>>> small-rpc, server startedCallback error.", e);
            }
        }
    }

    /**
     * stop server
     *
     * @throws Exception
     */
    public abstract void stop() throws Exception;

    /**
     * callback when stoped
     */
    public void onStoped() {
        if (stopedCallback != null) {
            try {
                stopedCallback.run();
            } catch (Exception e) {
                logger.error(">>>>>>>>>>> small-rpc, server stopedCallback error.", e);
            }
        }
    }

}
