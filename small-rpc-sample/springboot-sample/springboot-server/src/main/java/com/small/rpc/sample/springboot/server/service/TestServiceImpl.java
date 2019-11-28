package com.small.rpc.sample.springboot.server.service;

import com.small.rpc.remoting.provider.annotation.SmallRpcService;
import com.small.rpc.sample.springboot.api.TestService;
import org.springframework.stereotype.Service;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/27/19 2:43 PM
 */
@SmallRpcService
@Service
public class TestServiceImpl implements TestService {

    @Override
    public void test() {
        System.out.println("test");
    }
}
