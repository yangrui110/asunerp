
package com.hanlin.fadp.minilang.method.entityops;

import java.util.Map;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;set-pk-fields&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Csetpkfields%3E}}">Mini-language Reference</a>
 */
public final class SetPkFields extends MethodOperation {

    private final FlexibleMapAccessor<Map<String, ? extends Object>> mapFma;
    private final FlexibleStringExpander setIfNullFse;
    private final FlexibleMapAccessor<GenericValue> valueFma;

    public SetPkFields(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "value-field", "set-if-null", "map");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "value-field", "map");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "value-field", "map");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        valueFma = FlexibleMapAccessor.getInstance(element.getAttribute("value-field"));
        setIfNullFse = FlexibleStringExpander.getInstance(element.getAttribute("set-if-null"));
        mapFma = FlexibleMapAccessor.getInstance(element.getAttribute("map"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        GenericValue value = valueFma.get(methodContext.getEnvMap());
        if (value == null) {
            throw new MiniLangRuntimeException("Entity value not found with name: " + valueFma, this);
        }
        Map<String, ? extends Object> theMap = mapFma.get(methodContext.getEnvMap());
        if (theMap == null) {
            throw new MiniLangRuntimeException("Map not found with name: " + mapFma, this);
        }
        boolean setIfNull = !"false".equals(setIfNullFse.expand(methodContext.getEnvMap()));
        value.setPKFields(theMap, setIfNull);
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<set-pk-fields ");
        sb.append("value-field=\"").append(this.valueFma).append("\" ");
        sb.append("map=\"").append(this.mapFma).append("\" ");
        if (!setIfNullFse.isEmpty()) {
            sb.append("set-if-null=\"").append(this.setIfNullFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;set-pk-fields&gt; element.
     */
    public static final class SetPkFieldsFactory implements Factory<SetPkFields> {
        @Override
        public SetPkFields createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new SetPkFields(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "set-pk-fields";
        }
    }
}
