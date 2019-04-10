
package com.hanlin.fadp.service;

import java.util.Map;
import java.io.Serializable;

/**
 * Generic Requester Interface
 */
public interface GenericRequester extends Serializable {

    /**
     * Receive the result of an asynchronous service call
     * @param result Map of name, value pairs composing the result
     */
    public void receiveResult(Map<String, Object> result);

    /**
     * Receive an exception (Throwable) from an asynchronous service cell
     * @param t The Throwable which was received
     */
    public void receiveThrowable(Throwable t);
}

