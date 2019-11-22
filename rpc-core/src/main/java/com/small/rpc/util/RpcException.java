package com.small.rpc.util;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/22/19 6:00 PM
 */
public class RpcException extends RuntimeException {
    private static final long serialVersionUID = 42L;

    public RpcException(String msg) {
        super(msg);
    }

    public RpcException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
