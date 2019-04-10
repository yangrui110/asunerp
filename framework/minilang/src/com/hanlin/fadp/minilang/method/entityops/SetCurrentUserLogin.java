
package com.hanlin.fadp.minilang.method.entityops;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;set-current-user-login&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Csetcurrentuserlogin%3E}}">Mini-language Reference</a>
 */
public final class SetCurrentUserLogin extends MethodOperation {

    private final FlexibleMapAccessor<GenericValue> valueFma;

    public SetCurrentUserLogin(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.handleError("Deprecated - use the called service's userLogin IN attribute", simpleMethod, element);
            MiniLangValidate.attributeNames(simpleMethod, element, "value-field");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "value-field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "value-field");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        valueFma = FlexibleMapAccessor.getInstance(element.getAttribute("value-field"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        GenericValue userLogin = valueFma.get(methodContext.getEnvMap());
        if (userLogin == null) {
            throw new MiniLangRuntimeException("Entity value not found with name: " + valueFma, this);
        }
        methodContext.setUserLogin(userLogin, this.simpleMethod.getUserLoginEnvName());
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<set-current-user-login ");
        sb.append("value-field=\"").append(this.valueFma).append("\" />");
        return sb.toString();
    }

    /**
     * A factory for the &lt;set-current-user-login&gt; element.
     */
    public static final class SetCurrentUserLoginFactory implements Factory<SetCurrentUserLogin> {
        @Override
        public SetCurrentUserLogin createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new SetCurrentUserLogin(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "set-current-user-login";
        }
    }
}
