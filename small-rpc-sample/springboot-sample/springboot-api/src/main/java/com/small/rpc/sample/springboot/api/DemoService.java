package com.small.rpc.sample.springboot.api;

import com.small.rpc.sample.springboot.api.dto.UserDTO;

public interface DemoService {

    public UserDTO sayHi(String name);
}
