
package com.hanlin.fadp.minilang.method.envops;

import java.util.LinkedList;
import java.util.List;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MessageElement;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;add-error&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cadderror%3E}}">Mini-language Reference</a>
*/
public final class AddError extends MethodOperation {

    private final FlexibleMapAccessor<List<String>> errorListFma;
    private final MessageElement messageElement;

    public AddError(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "error-list-name");
            MiniLangValidate.constantAttributes(simpleMethod, element, "error-list-name");
            MiniLangValidate.childElements(simpleMethod, element, "fail-message", "fail-property");
            MiniLangValidate.requireAnyChildElement(simpleMethod, element, "fail-message", "fail-property");
        }
        errorListFma = FlexibleMapAccessor.getInstance(MiniLangValidate.checkAttribute(element.getAttribute("error-list-name"), "error_list"));
        messageElement = MessageElement.fromParentElement(element, simpleMethod);
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        if (messageElement != null) {
            String message = messageElement.getMessage(methodContext);
            if (message != null) {
                List<String> messages = errorListFma.get(methodContext.getEnvMap());
                if (messages == null) {
                    messages = new LinkedList<String>();
                    errorListFma.put(methodContext.getEnvMap(), messages);
                }
                messages.add(message);
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<add-error ");
        if (!"error_list".equals(this.errorListFma.getOriginalName())) {
            sb.append("error-list-name=\"").append(this.errorListFma).append("\"");
        }
        if (messageElement != null) {
            sb.append(">").append(messageElement).append("</add-error>");
        } else {
            sb.append("/>");
        }
        return sb.toString();
    }

    /**
     * A factory for the &lt;add-error&gt; element.
    */
    public static final class AddErrorFactory implements Factory<AddError> {
        @Override
        public AddError createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new AddError(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "add-error";
        }
    }
}
