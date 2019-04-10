
package com.hanlin.fadp.minilang.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.minilang.MiniLangElement;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.SimpleMethod;
import org.w3c.dom.Element;

/**
 * An abstract class for Mini-language element models.
 */
public abstract class MethodOperation extends MiniLangElement {


    protected MethodOperation(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
    }

    /**
     * Executes the operation.
     * Returns <code>true</code> if script execution should continue, or
     * <code>false</code> if script execution should stop.
     * 
     * @throws MiniLangException */
    public abstract boolean exec(MethodContext methodContext) throws MiniLangException;

    /** Create a string representation of the operation, using the current context.
     * @deprecated No replacement.
     */
    @Deprecated
    public String expandedString(MethodContext methodContext) {
        return FlexibleStringExpander.expandString(toString(), methodContext.getEnvMap());
    }

    /** Create a string representation of the operation - similar to the original XML.
     * @deprecated Use {@link #toString()}.
     */
    @Deprecated
    public String rawString() {
        return toString();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface DeprecatedOperation {
        String value();
    }

    public interface Factory<M extends MethodOperation> {

        M createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException;

        String getName();
    }
}
