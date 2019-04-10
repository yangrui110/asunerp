
package com.hanlin.fadp.minilang.method;

import com.hanlin.fadp.minilang.MiniLangElement;
import com.hanlin.fadp.minilang.SimpleMethod;
import org.w3c.dom.Element;

/**
 * A single Object value to be used as a parameter or whatever
 */
public abstract class MethodObject<T> extends MiniLangElement {

    public MethodObject(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
    }

    /** Get the Object value */
    public abstract T getObject(MethodContext methodContext);

    /** Get the Class for the type of the object */
    public abstract Class<T> getTypeClass(MethodContext methodContext) throws ClassNotFoundException;

    /** Get the name for the type of the object */
    public abstract String getTypeName();
}
