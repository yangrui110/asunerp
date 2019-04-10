
package com.hanlin.fadp.service;

import java.util.Map;

public interface GenericServiceCallback {

    public boolean isEnabled();
    public void receiveEvent(Map<String, Object> context);
    public void receiveEvent(Map<String, Object> context, Map<String, Object> result);
    public void receiveEvent(Map<String, Object> context, Throwable error);
}
