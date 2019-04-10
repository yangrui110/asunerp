
package com.hanlin.fadp.minilang.method.entityops;

import java.util.List;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Implements the &lt;store-list&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cstorelist%3E}}">Mini-language Reference</a>
 */
public final class StoreList extends EntityOperation {

    public static final String module = StoreList.class.getName();
    private final FlexibleMapAccessor<List<GenericValue>> listFma;

    public StoreList(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "list", "do-cache-clear", "delegator-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "list");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "list", "delegator-name");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        listFma = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        List<GenericValue> values = listFma.get(methodContext.getEnvMap());
        if (values == null) {
            throw new MiniLangRuntimeException("Entity value list not found with name: " + listFma, this);
        }
        try {
            Delegator delegator = getDelegator(methodContext);
            delegator.storeAll(values);
        } catch (GenericEntityException e) {
            String errMsg = "Exception thrown while storing entities: " + e.getMessage();
            Debug.logWarning(e, errMsg, module);
            simpleMethod.addErrorMessage(methodContext, errMsg);
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<store-list ");
        sb.append("list=\"").append(this.listFma).append("\" ");
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;store-list&gt; element.
     */
    public static final class StoreListFactory implements Factory<StoreList> {
        @Override
        public StoreList createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new StoreList(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "store-list";
        }
    }
}
