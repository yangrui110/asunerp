
package com.hanlin.fadp.minilang.method.envops;

import java.util.HashMap;
import java.util.Map;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;map-to-map&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cmaptomap%3E}}">Mini-language Reference</a>
 */
public final class MapToMap extends MethodOperation {

    private final FlexibleMapAccessor<Map<String, Object>> mapFma;
    private final FlexibleMapAccessor<Map<String, Object>> toMapFma;

    public MapToMap(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "to-map", "map");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "map");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "to-map", "map");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        mapFma = FlexibleMapAccessor.getInstance(element.getAttribute("map"));
        toMapFma = FlexibleMapAccessor.getInstance(element.getAttribute("to-map"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Map<String, Object> fromMap = mapFma.get(methodContext.getEnvMap());
        if (fromMap != null) {
            if (!toMapFma.isEmpty()) {
                Map<String, Object> toMap = toMapFma.get(methodContext.getEnvMap());
                if (toMap == null) {
                    toMap = new HashMap<String, Object>();
                    toMapFma.put(methodContext.getEnvMap(), toMap);
                }
               toMap.putAll(fromMap);
            } else {
                methodContext.putAllEnv(fromMap);
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<map-to-map ");
        sb.append("map=\"").append(this.mapFma).append("\" ");
        if (!toMapFma.isEmpty()) {
            sb.append("to-map=\"").append(this.toMapFma).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;map-to-map&gt; element.
     */
    public static final class MapToMapFactory implements Factory<MapToMap> {
        @Override
        public MapToMap createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new MapToMap(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "map-to-map";
        }
    }
}
