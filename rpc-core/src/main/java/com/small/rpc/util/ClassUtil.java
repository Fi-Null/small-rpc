package com.small.rpc.util;

import java.util.HashMap;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/22/19 5:42 PM
 */
public class ClassUtil {
    private static final HashMap<String, Class<?>> primClasses = new HashMap<>();

    static {
        primClasses.put("boolean", boolean.class);
        primClasses.put("byte", byte.class);
        primClasses.put("char", char.class);
        primClasses.put("short", short.class);
        primClasses.put("int", int.class);
        primClasses.put("long", long.class);
        primClasses.put("float", float.class);
        primClasses.put("double", double.class);
        primClasses.put("void", void.class);
    }

    public static Class<?> resolveClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            Class<?> cl = primClasses.get(className);
            if (cl != null) {
                return cl;
            } else {
                throw ex;
            }
        }
    }
}
