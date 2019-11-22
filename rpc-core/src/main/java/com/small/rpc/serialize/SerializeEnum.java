package com.small.rpc.serialize;

import com.small.rpc.serialize.hessian.HessianSerializer;
import com.small.rpc.serialize.jackson.JacksonSerializer;
import com.small.rpc.serialize.kryo.KryoSerializer;
import com.small.rpc.serialize.protostuff.ProtostuffSerializer;
import com.small.rpc.util.RpcException;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/22/19 8:57 PM
 */
public enum SerializeEnum {

    /**
     *
     */
    HESSIAN(HessianSerializer.class),
    /**
     *
     */
    PROTOSTUFF(ProtostuffSerializer.class),
    /**
     *
     */
    KRYO(KryoSerializer.class),
    /**
     *
     */
    JACKSON(JacksonSerializer.class);


    private Class<? extends Serializer> serializerClass;

    private SerializeEnum(Class<? extends Serializer> serializerClass) {
        this.serializerClass = serializerClass;
    }

    public Serializer getSerializer() {
        try {
            return serializerClass.newInstance();
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    public static SerializeEnum match(String name, SerializeEnum defaultSerializer) {
        for (SerializeEnum item : SerializeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultSerializer;
    }
}
