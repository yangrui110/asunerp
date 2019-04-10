
package com.hanlin.fadp.minilang;

/**
 * Thrown to indicate a Mini-language run-time error. 
 */
@SuppressWarnings("serial")
public class MiniLangRuntimeException extends MiniLangException {

    private final MiniLangElement element;

    public MiniLangRuntimeException(String str, MiniLangElement element) {
        super(str);
        this.element = element;
    }

    public MiniLangRuntimeException(Throwable nested, MiniLangElement element) {
        super(nested);
        this.element = element;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (this.element != null) {
            SimpleMethod method = this.element.getSimpleMethod();
            sb.append(" Method = ").append(method.getMethodName()).append(", File = ").append(method.getFromLocation());
            sb.append(", Element = <").append(this.element.getTagName()).append(">");
            sb.append(", Line ").append(this.element.getLineNumber());
        }
        return sb.toString();
    }
}
