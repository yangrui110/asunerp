
package com.hanlin.fadp.minilang.method.callops;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangUtil;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import org.w3c.dom.Element;

/**
 * Implements the &lt;set-service-fields&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Csetservicefields%3E}}">Mini-language Reference</a>
 */
public final class SetServiceFields extends MethodOperation {

    public static final String module = SetServiceFields.class.getName();

    // This method is needed only during the v1 to v2 transition
    private static boolean autoCorrect(Element element) {
        String errorListAttr = element.getAttribute("error-list-name");
        if (!errorListAttr.isEmpty()) {
            element.removeAttribute("error-list-name");
            return true;
        }
        return false;
    }

    private final FlexibleMapAccessor<Map<String, ? extends Object>> mapFma;
    private final FlexibleStringExpander serviceNameFse;
    private final FlexibleMapAccessor<Map<String, Object>> toMapFma;
    private final String mode;

    public SetServiceFields(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "service-name", "map", "to-map", "mode");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "service-name", "map", "to-map");
            MiniLangValidate.constantPlusExpressionAttributes(simpleMethod, element, "service-name");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "map", "to-map");
            MiniLangValidate.constantAttributes(simpleMethod, element, "mode");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        boolean elementModified = autoCorrect(element);
        if (elementModified && MiniLangUtil.autoCorrectOn()) {
            MiniLangUtil.flagDocumentAsCorrected(element);
        }
        serviceNameFse = FlexibleStringExpander.getInstance(element.getAttribute("service-name"));
        mapFma = FlexibleMapAccessor.getInstance(element.getAttribute("map"));
        toMapFma = FlexibleMapAccessor.getInstance(element.getAttribute("to-map"));
        mode = ModelService.OUT_PARAM.equals(element.getAttribute("mode")) ? ModelService.OUT_PARAM : ModelService.IN_PARAM;
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Map<String, ? extends Object> fromMap = mapFma.get(methodContext.getEnvMap());
        if (fromMap == null) {
            if (Debug.verboseOn()) {
                Debug.logVerbose("The from map in set-service-field was not found with name: " + mapFma, module);
            }
            return true;
        }
        String serviceName = serviceNameFse.expandString(methodContext.getEnvMap());
        ModelService modelService = null;
        try {
            modelService = methodContext.getDispatcher().getDispatchContext().getModelService(serviceName);
        } catch (GenericServiceException e) {
            throw new MiniLangRuntimeException("Could not get service definition for service name \"" + serviceName + "\": " + e.getMessage(), this);
        }
        Map<String, Object> toMap = toMapFma.get(methodContext.getEnvMap());
        if (toMap == null) {
            toMap = new HashMap<String, Object>();
            toMapFma.put(methodContext.getEnvMap(), toMap);
        }
        List<Object> errorMessages = new LinkedList<Object>();
        Map<String, Object> validAttributes = modelService.makeValid(fromMap, mode, true, errorMessages, methodContext.getTimeZone(), methodContext.getLocale());
        if (errorMessages.size() > 0) {
            for (Object obj : errorMessages) {
                simpleMethod.addErrorMessage(methodContext, (String) obj);
            }
            throw new MiniLangRuntimeException("Errors encountered while setting service attributes for service name \"" + serviceName + "\"", this);
        }
        toMap.putAll(validAttributes);
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        aic.addServiceName(this.serviceNameFse.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<set-service-fields ");
        if (!this.serviceNameFse.isEmpty()) {
            sb.append("service-name=\"").append(this.serviceNameFse).append("\" ");
        }
        if (!this.mapFma.isEmpty()) {
            sb.append("map=\"").append(this.mapFma).append("\" ");
        }
        if (!this.toMapFma.isEmpty()) {
            sb.append("to-map=\"").append(this.toMapFma).append("\" ");
        }
        sb.append("mode=\"").append(this.mode).append("\" ");
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;set-service-fields&gt; element.
     */
    public static final class SetServiceFieldsFactory implements Factory<SetServiceFields> {
        @Override
        public SetServiceFields createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new SetServiceFields(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "set-service-fields";
        }
    }
}
