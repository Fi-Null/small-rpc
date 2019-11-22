package com.small.rpc.serialize;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/22/19 8:33 PM
 */
public interface Serializer {

    public abstract <T> byte[] serialize(T obj);

    public abstract <T> Object deserialize(byte[] bytes, Class<T> clazz);

}
