package com.small.rpc.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 11/22/19 6:08 PM
 */
public class ThrowableUtil {
    /**
     * parse error to string
     *
     * @param e
     * @return
     */
    public static String toString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String errorMsg = stringWriter.toString();
        return errorMsg;
    }
}
