package com.small.rpc.remoting.route;

import java.util.Random;
import java.util.TreeSet;

/**
 * @ClassName RpcLoadBalanceRandomStrategy
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 00:31
 * @Version 1.0
 **/
public class RpcLoadBalanceRandomStrategy implements RpcLoadBalance {

    private Random random = new Random();

    @Override
    public String route(String serviceKey, TreeSet<String> addressSet) {
        // arr
        String[] addressArr = addressSet.toArray(new String[addressSet.size()]);

        // random
        String finalAddress = addressArr[random.nextInt(addressSet.size())];
        return finalAddress;
    }

}
