package com.small.rpc.remoting.provider.spring;

import com.small.rpc.remoting.provider.RpcProviderFactory;
import com.small.rpc.remoting.provider.annotation.SmallRpcService;
import com.small.rpc.util.RpcException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @ClassName SpringProviderFactory
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 19:28
 * @Version 1.0
 **/
public class SpringProviderFactory extends RpcProviderFactory implements ApplicationContextAware, InitializingBean, DisposableBean {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(SmallRpcService.class);
        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                // valid
                if (serviceBean.getClass().getInterfaces().length == 0) {
                    throw new RpcException("xxl-rpc, service(XxlRpcService) must inherit interface.");
                }
                // add service
                SmallRpcService smallRpcService = serviceBean.getClass().getAnnotation(SmallRpcService.class);

                String iface = serviceBean.getClass().getInterfaces()[0].getName();
                String version = smallRpcService.version();

                super.addService(iface, version, serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.start();
    }

    @Override
    public void destroy() throws Exception {
        super.stop();
    }
}
