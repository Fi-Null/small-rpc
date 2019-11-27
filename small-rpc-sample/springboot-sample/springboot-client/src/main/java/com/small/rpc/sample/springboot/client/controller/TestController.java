package com.small.rpc.sample.springboot.client.controller;

import com.small.rpc.remoting.invoker.annotation.SmallRpcReference;
import com.small.rpc.sample.springboot.api.DemoService;
import com.small.rpc.sample.springboot.api.dto.UserDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/27/19 2:19 PM
 */
public class TestController {

    @SmallRpcReference
    private DemoService demoService;


    @RequestMapping("/test")
    @ResponseBody
    public UserDTO http(String name) {

        try {
            return demoService.sayHi(name);
        } catch (Exception e) {
            e.printStackTrace();
            return new UserDTO(null, e.getMessage());
        }
    }
}
