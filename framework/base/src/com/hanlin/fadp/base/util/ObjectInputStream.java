
package com.hanlin.fadp.base.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;

/**
 * ObjectInputStream
 *
 */
public class ObjectInputStream extends java.io.ObjectInputStream {

    private ClassLoader classloader;

    public ObjectInputStream(InputStream in, ClassLoader loader) throws IOException {
        super(in);
        this.classloader = loader;
    }

    /**
     * @see java.io.ObjectInputStream#resolveClass(java.io.ObjectStreamClass)
     */
    @Override
    protected Class<?> resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
        return ObjectType.loadClass(classDesc.getName(), classloader);
    }

    /**
     * @see java.io.ObjectInputStream#resolveProxyClass(java.lang.String[])
     */
    @Override
    protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
        Class<?>[] cinterfaces = new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; i++)
            cinterfaces[i] = classloader.loadClass(interfaces[i]);

        try {
            return Proxy.getProxyClass(classloader, cinterfaces);
        } catch (IllegalArgumentException e) {
            throw new ClassNotFoundException(null, e);
        }

    }
}
