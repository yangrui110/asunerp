
package com.hanlin.fadp.minilang.method;

import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.SimpleMethod;
import org.w3c.dom.Element;

/**
 * Implements the &lt;field&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cfield%3E}}">Mini-language Reference</a>
 */
public final class FieldObject<T> extends MethodObject<T> {

    private final FlexibleMapAccessor<Object> fieldFma;
    private final String type;

    public FieldObject(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        String typeAttribute = element.getAttribute("type");
        if (typeAttribute.isEmpty()) {
            this.type = "java.lang.String";
        } else {
            this.type = typeAttribute;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject(MethodContext methodContext) {
        return (T) this.fieldFma.get(methodContext.getEnvMap());
    }

    @Override
    public Class<T> getTypeClass(MethodContext methodContext) throws ClassNotFoundException {
        return UtilGenerics.cast(ObjectType.loadClass(this.type, methodContext.getLoader()));
    }

    @Override
    public String getTypeName() {
        return this.type;
    }
}
