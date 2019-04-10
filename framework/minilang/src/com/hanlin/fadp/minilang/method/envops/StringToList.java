
package com.hanlin.fadp.minilang.method.envops;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import com.hanlin.fadp.base.util.MessageString;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;string-to-list&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cstringtolist%3E}}">Mini-language Reference</a>
 */
public final class StringToList extends MethodOperation {

    private final FlexibleMapAccessor<List<? extends Object>> argListFma;
    private final FlexibleMapAccessor<List<Object>> listFma;
    private final String messageFieldName;
    private final FlexibleStringExpander stringFse;

    public StringToList(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.handleError("<string-to-list> element is deprecated (use <set>)", simpleMethod, element);
            MiniLangValidate.attributeNames(simpleMethod, element, "list", "arg-list", "string", "message-field");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "list", "string");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "list", "arg-list");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        stringFse = FlexibleStringExpander.getInstance(element.getAttribute("string"));
        listFma = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
        argListFma = FlexibleMapAccessor.getInstance(element.getAttribute("arg-list"));
        messageFieldName = element.getAttribute("message-field");
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        String valueStr = stringFse.expandString(methodContext.getEnvMap());
        List<? extends Object> argList = argListFma.get(methodContext.getEnvMap());
        if (argList != null) {
            try {
                valueStr = MessageFormat.format(valueStr, argList.toArray());
            } catch (IllegalArgumentException e) {
                throw new MiniLangRuntimeException("Exception thrown while formatting the string attribute: " + e.getMessage(), this);
            }
        }
        Object value;
        if (UtilValidate.isNotEmpty(this.messageFieldName)) {
            value = new MessageString(valueStr, this.messageFieldName, true);
        } else {
            value = valueStr;
        }
        List<Object> toList = listFma.get(methodContext.getEnvMap());
        if (toList == null) {
            toList = new LinkedList<Object>();
            listFma.put(methodContext.getEnvMap(), toList);
        }
        toList.add(value);
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<string-to-list ");
        sb.append("string=\"").append(this.stringFse).append("\" ");
        sb.append("list=\"").append(this.listFma).append("\" ");
        if (!this.argListFma.isEmpty()) {
            sb.append("arg-list=\"").append(this.argListFma).append("\" ");
        }
        if (!this.messageFieldName.isEmpty()) {
            sb.append("message-field=\"").append(this.messageFieldName).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;string-to-list&gt; element.
     */
    public static final class StringToListFactory implements Factory<StringToList> {
        @Override
        public StringToList createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new StringToList(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "string-to-list";
        }
    }
}
