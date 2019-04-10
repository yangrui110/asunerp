
package com.hanlin.fadp.minilang.method.callops;

import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;return&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Creturn%3E}}">Mini-language Reference</a>
 */
public final class Return extends MethodOperation {

    private final FlexibleStringExpander responseCodeFse;

    public Return(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "response-code");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        responseCodeFse = FlexibleStringExpander.getInstance(element.getAttribute("response-code"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        String responseCode = responseCodeFse.expandString(methodContext.getEnvMap());
        if (responseCode.isEmpty()) {
            responseCode = simpleMethod.getDefaultSuccessCode();
        }
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            methodContext.putEnv(simpleMethod.getEventResponseCodeName(), responseCode);
        } else {
            methodContext.putEnv(simpleMethod.getServiceResponseMessageName(), responseCode);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<return ");
        if (!"success".equals(responseCodeFse.getOriginal())) {
            sb.append("response-code=\"").append(responseCodeFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;return&gt; element.
     */
    public static final class ReturnFactory implements Factory<Return> {
        @Override
        public Return createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new Return(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "return";
        }
    }
}
