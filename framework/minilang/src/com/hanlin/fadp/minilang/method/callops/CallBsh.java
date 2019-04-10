
package com.hanlin.fadp.minilang.method.callops;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.StringUtil;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangUtil;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Implements the &lt;call-bsh&gt; element.
 *
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Ccallbsh%3E}}">Mini-language Reference</a>
 */
public final class CallBsh extends MethodOperation {

    public static final String module = CallBsh.class.getName();

    // This method is needed only during the v1 to v2 transition
    private static boolean autoCorrect(Element element) {
        boolean elementModified = false;
        String errorListAttr = element.getAttribute("error-list-name");
        if (errorListAttr.length() > 0) {
            element.removeAttribute("error-list-name");
            elementModified = true;
        }
        return elementModified;
    }

    private final String inline;
    private final String resource;

    public CallBsh(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.handleError("<call-bsh> element is deprecated (use <script>)", simpleMethod, element);
            MiniLangValidate.attributeNames(simpleMethod, element, "resource");
            MiniLangValidate.constantAttributes(simpleMethod, element, "resource");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        boolean elementModified = autoCorrect(element);
        if (elementModified && MiniLangUtil.autoCorrectOn()) {
            MiniLangUtil.flagDocumentAsCorrected(element);
        }
        this.inline = StringUtil.convertOperatorSubstitutions(UtilXml.elementValue(element));
        this.resource = element.getAttribute("resource");
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Interpreter bsh = new Interpreter();
        bsh.setClassLoader(methodContext.getLoader());
        try {
            // setup environment
            for (Map.Entry<String, Object> entry : methodContext.getEnvMap().entrySet()) {
                bsh.set(entry.getKey(), entry.getValue());
            }
            // run external, from resource, first if resource specified
            if (UtilValidate.isNotEmpty(this.resource)) {
                InputStream is = methodContext.getLoader().getResourceAsStream(this.resource);
                if (is == null) {
                    this.simpleMethod.addErrorMessage(methodContext, "Could not find bsh resource: " + this.resource);
                } else {
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(is));
                        StringBuilder outSb = new StringBuilder();
                        String tempStr = null;
                        while ((tempStr = reader.readLine()) != null) {
                            outSb.append(tempStr);
                            outSb.append('\n');
                        }
                        Object resourceResult = bsh.eval(outSb.toString());
                        // if map is returned, copy values into env
                        if ((resourceResult != null) && (resourceResult instanceof Map<?, ?>)) {
                            methodContext.putAllEnv(UtilGenerics.<String, Object> checkMap(resourceResult));
                        }
                    } catch (IOException e) {
                        this.simpleMethod.addErrorMessage(methodContext, "IO error loading bsh resource: " + e.getMessage());
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                this.simpleMethod.addErrorMessage(methodContext, "IO error closing BufferedReader: " + e.getMessage());
                            }
                        }
                    }
                }
            }
            if (Debug.verboseOn())
                Debug.logVerbose("Running inline BSH script: " + inline, module);
            // run inlined second to it can override the one from the property
            Object inlineResult = bsh.eval(inline);
            if (Debug.verboseOn())
                Debug.logVerbose("Result of inline BSH script: " + inlineResult, module);
            // if map is returned, copy values into env
            if ((inlineResult != null) && (inlineResult instanceof Map<?, ?>)) {
                methodContext.putAllEnv(UtilGenerics.<String, Object> checkMap(inlineResult));
            }
        } catch (EvalError e) {
            Debug.logWarning(e, "BeanShell execution caused an error", module);
            this.simpleMethod.addErrorMessage(methodContext, "BeanShell execution caused an error: " + e.getMessage());
        }
        // always return true, error messages just go on the error list
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<set ");
        if (this.resource.length() > 0) {
            sb.append("resource=\"").append(this.resource).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;call-bsh&gt; element.
     */
    public static final class CallBshFactory implements Factory<CallBsh> {
        @Override
        public CallBsh createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CallBsh(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "call-bsh";
        }
    }
}
