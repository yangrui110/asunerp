
package com.hanlin.fadp.minilang.method.envops;

import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;break&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cbreak%3E}}">Mini-language Reference</a>
 */
public class Break extends MethodOperation {

    public Break(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        throw new BreakElementException();
    }

    @Override
    public String toString() {
        return "<break/>";
    }

    @SuppressWarnings("serial")
    public class BreakElementException extends MiniLangException {

        public BreakElementException() {
            super("<break> element encountered without enclosing loop");
        }

        @Override
        public String getMessage() {
            StringBuilder sb = new StringBuilder(super.getMessage());
            SimpleMethod method = getSimpleMethod();
            sb.append(" Method = ").append(method.getMethodName()).append(", File = ").append(method.getFromLocation());
            sb.append(", Element = <break>, Line ").append(getLineNumber());
            return sb.toString();
        }
    }

    /**
     * A factory for the &lt;break&gt; element.
     */
    public static final class BreakFactory implements Factory<Break> {
        @Override
        public Break createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new Break(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "break";
        }
    }
}
