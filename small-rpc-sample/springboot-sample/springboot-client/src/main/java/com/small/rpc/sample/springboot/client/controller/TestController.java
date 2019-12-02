package com.small.rpc.sample.springboot.client.controller;

import com.small.rpc.remoting.invoker.annotation.SmallRpcReference;
import com.small.rpc.remoting.invoker.call.CallType;
import com.small.rpc.remoting.invoker.call.RpcInvokeCallback;
import com.small.rpc.remoting.invoker.call.RpcInvokeFuture;
import com.small.rpc.remoting.invoker.reference.RpcReferenceBean;
import com.small.rpc.remoting.invoker.route.LoadBalance;
import com.small.rpc.remoting.net.netty.client.NettyClient;
import com.small.rpc.sample.springboot.api.DemoService;
import com.small.rpc.sample.springboot.api.TestService;
import com.small.rpc.sample.springboot.api.dto.UserDTO;
import com.small.rpc.serialize.hessian.HessianSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.Future;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/27/19 2:19 PM
 */
@Controller
public class TestController {

    /**
     * CallType.SYNC
     */
    //@SmallRpcReference(callType = CallType.SYNC, timeout = 2000)
    //@SmallRpcReference(callType = CallType.FUTURE, timeout = 2000)
    @SmallRpcReference(callType = CallType.CALLBACK, timeout = 2000)
    private DemoService demoService;

    @SmallRpcReference
    private TestService testService;


    @RequestMapping("/test")
    @ResponseBody
    public UserDTO http(String name) {

        try {

            /**
             * CallType.SYNC
             */
            UserDTO userDTO = new UserDTO();
            userDTO = demoService.sayHi(name);

            /**
             * CallType.FUTURE
             */
            demoService.sayHi(name);
            Future<UserDTO> userDTOFuture = RpcInvokeFuture.getFuture(UserDTO.class);
            userDTO = userDTOFuture.get();

            /**
             * CallType.CALLBACK
             */
            RpcInvokeCallback.setCallback(new RpcInvokeCallback<UserDTO>() {
                @Override
                public void onSuccess(UserDTO result) {
                    System.out.println(result);
                }

                @Override
                public void onFailure(Throwable exception) {
                    exception.printStackTrace();
                }
            });
            demoService.sayHi("[CALLBACK]jack");

            /**
             * CallType.ONEWAY(direct invoke by remote ip)
             */
            RpcReferenceBean referenceBean = new RpcReferenceBean();
            referenceBean.setClient(NettyClient.class);
            referenceBean.setSerializer(HessianSerializer.class);
            referenceBean.setCallType(CallType.ONEWAY);
            referenceBean.setLoadBalance(LoadBalance.ROUND);
            referenceBean.setIface(DemoService.class);
            referenceBean.setVersion(null);
            referenceBean.setTimeout(500);
            referenceBean.setAddress("127.0.0.1:7080");
            referenceBean.setAccessToken(null);
            referenceBean.setInvokeCallback(null);
            referenceBean.setInvokerFactory(null);

            DemoService demoService = (DemoService) referenceBean.getObject();
            demoService.sayHi("[ONEWAY]jack");


            return userDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return new UserDTO(null, e.getMessage());
        }
    }
}
