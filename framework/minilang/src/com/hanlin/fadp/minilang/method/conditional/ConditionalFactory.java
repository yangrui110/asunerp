
package com.hanlin.fadp.minilang.method.conditional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.SimpleMethod;
import org.w3c.dom.Element;

/**
 * An abstract factory class for creating &lt;if&gt; element sub-element implementations.
 * <p>Mini-language can be extended to support additional condition elements
 * by extending this class to provide custom conditional element implementations.
 * </p>
 */
public abstract class ConditionalFactory<C extends Conditional> {

    public static final String module = ConditionalFactory.class.getName();
    private static final Map<String, ConditionalFactory<?>> conditionalFactories;

    static {
        Map<String, ConditionalFactory<?>> factories = new HashMap<String, ConditionalFactory<?>>();
        Iterator<ConditionalFactory<?>> it = UtilGenerics.cast(ServiceLoader.load(ConditionalFactory.class, ConditionalFactory.class.getClassLoader()).iterator());
        while (it.hasNext()) {
            ConditionalFactory<?> factory = it.next();
            factories.put(factory.getName(), factory);
        }
        conditionalFactories = Collections.unmodifiableMap(factories);
    }

    public static Conditional makeConditional(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        String tagName = element.getTagName();
        ConditionalFactory<?> factory = conditionalFactories.get(tagName);
        if (factory != null) {
            return factory.createCondition(element, simpleMethod);
        } else {
            Debug.logWarning("Found an unknown if condition: " + tagName, module);
            return null;
        }
    }

    public abstract C createCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException;

    public abstract String getName();
}
