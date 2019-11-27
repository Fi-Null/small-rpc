package com.small.rpc.sample.springboot.server.conf;

import com.small.rpc.registry.smallregistry.SmallRegistryServiceRegistry;
import com.small.rpc.remoting.net.netty.server.NettyServer;
import com.small.rpc.remoting.provider.spring.SpringProviderFactory;
import com.small.rpc.serialize.hessian.HessianSerializer;
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
 * @createDate 11/27/19 11:00 AM
 */
@Configuration
public class SmallRpcProviderConfig {

    private Logger logger = LoggerFactory.getLogger(SmallRpcProviderConfig.class);

    @Value("${small-rpc.remoting.port}")
    private int port;

    @Value("${small-rpc.registry.smallregistry.address}")
    private String address;

    @Value("${small-rpc.registry.smallregistry.env}")
    private String env;

    @Bean
    public SpringProviderFactory rpcSpringProviderFactory() {

        SpringProviderFactory providerFactory = new SpringProviderFactory();
        providerFactory.setServer(NettyServer.class);
        providerFactory.setSerializer(HessianSerializer.class);
        providerFactory.setCorePoolSize(-1);
        providerFactory.setMaxPoolSize(-1);
        providerFactory.setIp(null);
        providerFactory.setPort(port);
        providerFactory.setAccessToken(null);
        providerFactory.setServiceRegistry(SmallRegistryServiceRegistry.class);
        providerFactory.setServiceRegistryParam(new HashMap<String, String>() {{
            put(SmallRegistryServiceRegistry.SMALL_REGISTRY_ADDRESS, address);
            put(SmallRegistryServiceRegistry.ENV, env);
        }});

        logger.info(">>>>>>>>>>> small-rpc provider config init finish.");
        return providerFactory;
    }
}
