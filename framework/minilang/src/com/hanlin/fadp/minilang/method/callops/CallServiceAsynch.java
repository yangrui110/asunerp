
package com.hanlin.fadp.minilang.method.callops;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import com.hanlin.fadp.service.GenericServiceException;
import org.w3c.dom.Element;

/**
 * Implements the &lt;call-service-asynch&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Ccallserviceasynch%3E}}">Mini-language Reference</a>
 */
public final class CallServiceAsynch extends MethodOperation {

    public static final String module = CallServiceAsynch.class.getName();

    private final boolean includeUserLogin;
    private final FlexibleMapAccessor<Map<String, Object>> inMapFma;
    private final FlexibleStringExpander serviceNameFse;

    public CallServiceAsynch(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "serviceName", "in-map-name", "include-user-login");
            MiniLangValidate.constantAttributes(simpleMethod, element, "include-user-login");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "service-name", "in-map-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "service-name");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        serviceNameFse = FlexibleStringExpander.getInstance(element.getAttribute("service-name"));
        inMapFma = FlexibleMapAccessor.getInstance(element.getAttribute("in-map-name"));
        includeUserLogin = !"false".equals(element.getAttribute("include-user-login"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        if (methodContext.isTraceOn()) {
            outputTraceMessage(methodContext, "Begin call-service-asynch.");
        }
        String serviceName = serviceNameFse.expandString(methodContext.getEnvMap());
        Map<String, Object> inMap = inMapFma.get(methodContext.getEnvMap());
        if (inMap == null) {
            inMap = new HashMap<String, Object>();
        }
        if (includeUserLogin) {
            GenericValue userLogin = methodContext.getUserLogin();
            if (userLogin != null && inMap.get("userLogin") == null) {
                inMap.put("userLogin", userLogin);
            }
        }
        Locale locale = methodContext.getLocale();
        if (locale != null) {
            inMap.put("locale", locale);
        }
        try {
            if (methodContext.isTraceOn()) {
                outputTraceMessage(methodContext, "Invoking service \"" + serviceName + "\", IN attributes:", inMap.toString());
            }
            methodContext.getDispatcher().runAsync(serviceName, inMap);
        } catch (GenericServiceException e) {
            if (methodContext.isTraceOn()) {
                outputTraceMessage(methodContext, "Service engine threw an exception: " + e.getMessage() + ", halting script execution. End call-service-asynch.");
            }
            Debug.logError(e, module);
            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [problem invoking the " + serviceName + " service: " + e.getMessage() + "]";
            if (methodContext.getMethodType() == MethodContext.EVENT) {
                methodContext.putEnv(simpleMethod.getEventErrorMessageName(), errMsg);
                methodContext.putEnv(simpleMethod.getEventResponseCodeName(), simpleMethod.getDefaultErrorCode());
            } else {
                methodContext.putEnv(simpleMethod.getServiceErrorMessageName(), errMsg);
                methodContext.putEnv(simpleMethod.getServiceResponseMessageName(), simpleMethod.getDefaultErrorCode());
            }
            return false;
        }
        if (methodContext.isTraceOn()) {
            outputTraceMessage(methodContext, "End call-service-asynch.");
        }
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        aic.addServiceName(this.serviceNameFse.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<call-service-asynch ");
        sb.append("service-name=\"").append(this.serviceNameFse).append("\" ");
        if (!this.inMapFma.isEmpty()) {
            sb.append("in-map-name=\"").append(this.inMapFma).append("\" ");
        }
        if (!this.includeUserLogin) {
            sb.append("include-user-login=\"false\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;call-service-asynch&gt; element.
     */
    public static final class CallServiceAsynchFactory implements Factory<CallServiceAsynch> {
        @Override
        public CallServiceAsynch createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CallServiceAsynch(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "call-service-asynch";
        }
    }
}
