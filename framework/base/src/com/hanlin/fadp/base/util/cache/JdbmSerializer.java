
package com.hanlin.fadp.base.util.cache;

import java.io.IOException;

import jdbm.RecordManager;
import jdbm.helper.ISerializationHandler;
import jdbm.helper.Serializer;

import com.hanlin.fadp.base.util.UtilObject;

/**
 * JDBC Serializer which uses OFBiz internal serialization
 * (needed do to the fact that we do dynamic class loading)
 *
 */
public class JdbmSerializer implements Serializer, ISerializationHandler {

    public byte[] serialize(Object o) throws IOException {
        return UtilObject.getBytes(o);
    }

    public byte[] serialize(RecordManager recman, long recid, Object o) throws IOException {
        return UtilObject.getBytes(o);
    }

    public Object deserialize(byte[] bytes) throws IOException {
        return UtilObject.getObject(bytes);
    }

    public Object deserialize(RecordManager recman, long recid, byte[] bytes) throws IOException {
        return UtilObject.getObject(bytes);
    }
}
