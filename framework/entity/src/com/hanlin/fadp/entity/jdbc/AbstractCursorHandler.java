
package com.hanlin.fadp.entity.jdbc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class AbstractCursorHandler implements InvocationHandler {

    protected String cursorName;
    protected int fetchSize;

    protected AbstractCursorHandler(String cursorName, int fetchSize) {
        this.cursorName = cursorName;
        this.fetchSize = fetchSize;
    }

    public void setCursorName(String cursorName) {
        this.cursorName = cursorName;
    }

    public String getCursorName() {
        return cursorName;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    protected Object invoke(Object obj, Object proxy, Method method, Object... args) throws Throwable {
        if ("toString".equals(method.getName())) {
            String str = obj.toString();
            return getClass().getName() + "{" + str + "}";
        }
        return method.invoke(obj, args);
    }

    protected static <T> T newHandler(InvocationHandler handler, Class<T> implClass) throws IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException {
        ClassLoader loader = implClass.getClassLoader();
        if (loader == null) loader = ClassLoader.getSystemClassLoader();
        Class<?> proxyClass = Proxy.getProxyClass(loader, implClass);
        Constructor<?> constructor = proxyClass.getConstructor(InvocationHandler.class);
        return implClass.cast(constructor.newInstance(handler));
    }
}
