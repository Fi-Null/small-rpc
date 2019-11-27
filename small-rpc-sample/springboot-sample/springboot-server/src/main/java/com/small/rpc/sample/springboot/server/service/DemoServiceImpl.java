package com.small.rpc.sample.springboot.server.service;

import com.small.rpc.remoting.provider.annotation.SmallRpcService;
import com.small.rpc.sample.springboot.api.DemoService;
import com.small.rpc.sample.springboot.api.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/27/19 11:32 AM
 *
 */
@SmallRpcService
@Service
public class DemoServiceImpl implements DemoService {

    private static Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    @Override
    public UserDTO sayHi(String name) {

        String word = MessageFormat.format("Hi {0}, from {1} as {2}",
                name, DemoServiceImpl.class.getName(), String.valueOf(System.currentTimeMillis()));

        if ("error".equalsIgnoreCase(name)) {
            throw new RuntimeException("test exception.");
        }

        UserDTO userDTO = new UserDTO(name, word);
        logger.info(userDTO.toString());

        return userDTO;
    }
}
