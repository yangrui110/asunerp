
package com.hanlin.fadp.minilang.method.callops;

import com.hanlin.fadp.base.util.ScriptUtil;
import com.hanlin.fadp.base.util.Scriptlet;
import com.hanlin.fadp.base.util.StringUtil;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangUtil;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;script&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cscript%3E}}">Mini-language Reference</a>
 */
public final class CallScript extends MethodOperation {

    public static final String module = CallScript.class.getName();

    // This method is needed only during the v1 to v2 transition
    private static boolean autoCorrect(Element element) {
        String errorListAttr = element.getAttribute("error-list-name");
        if (errorListAttr.length() > 0) {
            element.removeAttribute("error-list-name");
            return true;
        }
        return false;
    }
    
    /*
     * Developers - the location attribute is a constant for security reasons.
     * Script invocations should always be hard-coded.
     */
    private final String location;
    private final String method;
    private final Scriptlet scriptlet;

    public CallScript(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "location", "script");
            MiniLangValidate.requireAnyAttribute(simpleMethod, element, "location", "script");
            MiniLangValidate.constantAttributes(simpleMethod, element, "location");
            MiniLangValidate.scriptAttributes(simpleMethod, element, "script");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        boolean elementModified = autoCorrect(element);
        if (elementModified && MiniLangUtil.autoCorrectOn()) {
            MiniLangUtil.flagDocumentAsCorrected(element);
        }
        String scriptLocation = element.getAttribute("location");
        if (scriptLocation.isEmpty()) {
            this.location = null;
            this.method = null;
        } else {
            int pos = scriptLocation.lastIndexOf("#");
            if (pos == -1) {
                this.location = scriptLocation;
                this.method = null;
            } else {
                this.location = scriptLocation.substring(0, pos);
                this.method = scriptLocation.substring(pos + 1);
            }
        }
        String inlineScript = element.getAttribute("script");
        if (inlineScript.isEmpty()) {
            inlineScript = UtilXml.elementValue(element);
        }
        if (inlineScript != null && MiniLangUtil.containsScript(inlineScript)) {
            this.scriptlet = new Scriptlet(StringUtil.convertOperatorSubstitutions(inlineScript));
        } else {
            this.scriptlet = null;
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        if (this.location != null) {
            if (location.endsWith(".xml")) {
                SimpleMethod.runSimpleMethod(location, method, methodContext);
            } else {
                ScriptUtil.executeScript(this.location, this.method, methodContext.getEnvMap());
            }
        }
        if (this.scriptlet != null) {
            try {
                this.scriptlet.executeScript(methodContext.getEnvMap());
            } catch (Exception e) {
                throw new MiniLangRuntimeException(e.getMessage(), this);
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<script ");
        if (this.location != null) {
            sb.append("location=\"").append(this.location);
            if (this.method != null) {
                sb.append("#").append(this.method);
            }
            sb.append("\" ");
        }
        if (this.scriptlet != null) {
            sb.append("script=\"").append(this.scriptlet).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;script&gt; element.
     */
    public static final class CallScriptFactory implements Factory<CallScript> {
        @Override
        public CallScript createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CallScript(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "script";
        }
    }
}
