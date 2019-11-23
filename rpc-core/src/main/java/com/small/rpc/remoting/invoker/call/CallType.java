package com.small.rpc.remoting.invoker.call;

/**
 * @ClassName CallType
 * @Description TODO
 * @Author xiangke
 * @Date 2019/11/24 00:24
 * @Version 1.0
 **/
public enum CallType {


    SYNC,

    FUTURE,

    CALLBACK,

    ONEWAY;


    public static CallType match(String name, CallType defaultCallType) {
        for (CallType item : CallType.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultCallType;
    }

}
