
package com.hanlin.fadp.minilang;

import org.w3c.dom.Element;

/**
 * Thrown to indicate that a Mini-language element is invalid. 
 */
@SuppressWarnings("serial")
public class ValidationException extends MiniLangException {

    private final SimpleMethod method;
    private final Element element;

    public ValidationException(String str, SimpleMethod method, Element element) {
        super(str);
        this.method = method;
        this.element = element;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (method != null) {
            sb.append(" Method = ").append(method.getMethodName()).append(", File = ").append(method.getFromLocation());
        }
        if (element != null) {
            sb.append(", Element = <").append(element.getTagName()).append(">");
            Object lineNumber = element.getUserData("startLine");
            if (lineNumber != null) {
                sb.append(", Line ").append(lineNumber);
            }
        }
        return sb.toString();
    }
}
