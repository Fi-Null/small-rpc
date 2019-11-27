package com.small.rpc.sample.springboot.client.config;

import com.small.rpc.registry.smallregistry.SmallRegistryServiceRegistry;
import com.small.rpc.remoting.invoker.spring.RpcSpringInvokerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/27/19 2:15 PM
 */
@Configuration
public class SmallRpcInvokerConfig {
    private Logger logger = LoggerFactory.getLogger(SmallRpcInvokerConfig.class);

    @Value("${small-rpc.registry.smallregistry.address}")
    private String address;

    @Value("${small-rpc.registry.smallregistry.env}")
    private String env;

    @Bean
    public RpcSpringInvokerFactory rpcSpringProviderFactory() {

        RpcSpringInvokerFactory invokerFactory = new RpcSpringInvokerFactory();
        invokerFactory.setServiceRegistryClass(SmallRegistryServiceRegistry.class);
        invokerFactory.setServiceRegistryParam(new HashMap<String, String>(){{
            put(SmallRegistryServiceRegistry.SMALL_REGISTRY_ADDRESS, address);
            put(SmallRegistryServiceRegistry.ENV, env);
        }});

        logger.info(">>>>>>>>>>> small-rpc invoker config init finish.");
        return invokerFactory;
    }
}
