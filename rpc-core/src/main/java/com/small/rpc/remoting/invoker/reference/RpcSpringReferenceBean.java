package com.small.rpc.remoting.invoker.reference;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/26/19 5:50 PM
 */
public class RpcSpringReferenceBean implements FactoryBean<Object>, InitializingBean {


    // ---------------------- util ----------------------

    private RpcReferenceBean rpcReferenceBean;

    @Override
    public void afterPropertiesSet() {

        // init config
        this.rpcReferenceBean = new RpcReferenceBean();
    }


    @Override
    public Object getObject() throws Exception {
        return rpcReferenceBean.getObject();
    }

    @Override
    public Class<?> getObjectType() {
        return rpcReferenceBean.getIface();
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
