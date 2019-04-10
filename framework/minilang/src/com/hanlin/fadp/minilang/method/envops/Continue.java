
package com.hanlin.fadp.minilang.method.envops;

import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;continue&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Ccontinue%3E}}">Mini-language Reference</a>
 */
public class Continue extends MethodOperation {

    public Continue(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        throw new ContinueElementException();
    }

    @Override
    public String toString() {
        return "<continue/>";
    }

    @SuppressWarnings("serial")
    public class ContinueElementException extends MiniLangException {

        public ContinueElementException() {
            super("<continue> element encountered without enclosing loop");
        }

        @Override
        public String getMessage() {
            StringBuilder sb = new StringBuilder(super.getMessage());
            SimpleMethod method = getSimpleMethod();
            sb.append(" Method = ").append(method.getMethodName()).append(", File = ").append(method.getFromLocation());
            sb.append(", Element = <continue>, Line ").append(getLineNumber());
            return sb.toString();
        }
    }

    /**
     * A factory for the &lt;continue&gt; element.
     */
    public static final class ContinueFactory implements Factory<Continue> {
        @Override
        public Continue createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new Continue(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "continue";
        }
    }
}
