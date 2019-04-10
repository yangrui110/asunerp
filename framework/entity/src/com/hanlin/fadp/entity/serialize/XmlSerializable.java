
package com.hanlin.fadp.entity.serialize;

import org.w3c.dom.Element;

/**
 * XmlSerializable
 *
 */
public interface XmlSerializable<T> {

    /**
     * Deserialize the XML element back to an object
     * @param element XML element
     * @throws SerializeException
     */
    public T deserialize(Element element) throws SerializeException;

    /**
     * Serialize the object to an XML element
     * @throws SerializeException
     */
    public Element serialize() throws SerializeException;

}

