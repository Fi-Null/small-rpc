package com.small.rpc.serialize.jackson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.small.rpc.serialize.Serializer;
import com.small.rpc.util.RpcException;

import java.io.IOException;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/22/19 8:49 PM
 */
public class JacksonSerializer implements Serializer {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * bean、array、List、Map --> json
     *
     * @param <T>
     */
    @Override
    public <T> byte[] serialize(T obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RpcException(e);
        }
    }

    /**
     * string --> bean、Map、List(array)
     */
    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (JsonParseException e) {
            throw new RpcException(e);
        } catch (JsonMappingException e) {
            throw new RpcException(e);
        } catch (IOException e) {
            throw new RpcException(e);
        }
    }
}
