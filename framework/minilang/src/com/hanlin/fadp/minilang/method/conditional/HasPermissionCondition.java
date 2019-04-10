
package com.hanlin.fadp.minilang.method.conditional;

import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import com.hanlin.fadp.security.Security;
import org.w3c.dom.Element;

/**
 * Implements the &lt;if-has-permission&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cifhaspermission%3E}}">Mini-language Reference</a>
 */
public final class HasPermissionCondition extends MethodOperation implements Conditional {

    private final FlexibleStringExpander actionFse;
    private final FlexibleStringExpander permissionFse;
    // Sub-operations are used only when this is a method operation.
    private final List<MethodOperation> elseSubOps;
    private final List<MethodOperation> subOps;

    public HasPermissionCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "permission", "action");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "permission");
            MiniLangValidate.constantPlusExpressionAttributes(simpleMethod, element, "permission", "action");
        }
        this.permissionFse = FlexibleStringExpander.getInstance(element.getAttribute("permission"));
        this.actionFse = FlexibleStringExpander.getInstance(element.getAttribute("action"));
        Element childElement = UtilXml.firstChildElement(element);
        if (childElement != null && !"else".equals(childElement.getTagName())) {
            this.subOps = Collections.unmodifiableList(SimpleMethod.readOperations(element, simpleMethod));
        } else {
            this.subOps = null;
        }
        Element elseElement = UtilXml.firstChildElement(element, "else");
        if (elseElement != null) {
            this.elseSubOps = Collections.unmodifiableList(SimpleMethod.readOperations(elseElement, simpleMethod));
        } else {
            this.elseSubOps = null;
        }
    }

    @Override
    public boolean checkCondition(MethodContext methodContext) throws MiniLangException {
        GenericValue userLogin = methodContext.getUserLogin();
        if (userLogin != null) {
            Security security = methodContext.getSecurity();
            String permission = permissionFse.expandString(methodContext.getEnvMap());
            String action = actionFse.expandString(methodContext.getEnvMap());
            if (!action.isEmpty()) {
                if (security.hasEntityPermission(permission, action, userLogin)) {
                    return true;
                }
            } else {
                if (security.hasPermission(permission, userLogin)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        if (checkCondition(methodContext)) {
            if (this.subOps != null) {
                return SimpleMethod.runSubOps(subOps, methodContext);
            }
        } else {
            if (elseSubOps != null) {
                return SimpleMethod.runSubOps(elseSubOps, methodContext);
            }
        }
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        if (this.subOps != null) {
            for (MethodOperation method : this.subOps) {
                method.gatherArtifactInfo(aic);
            }
        }
        if (this.elseSubOps != null) {
            for (MethodOperation method : this.elseSubOps) {
                method.gatherArtifactInfo(aic);
            }
        }
    }

    public void prettyPrint(StringBuilder messageBuffer, MethodContext methodContext) {
        messageBuffer.append("has-permission[");
        messageBuffer.append(this.permissionFse);
        if (UtilValidate.isNotEmpty(this.actionFse)) {
            messageBuffer.append(":");
            messageBuffer.append(this.actionFse);
        }
        messageBuffer.append("]");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<if-has-permission ");
        if (!this.permissionFse.isEmpty()) {
            sb.append("permission=\"").append(this.permissionFse).append("\" ");
        }
        if (!this.actionFse.isEmpty()) {
            sb.append("action=\"").append(this.actionFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A &lt;if-has-permission&gt; element factory. 
     */
    public static final class HasPermissionConditionFactory extends ConditionalFactory<HasPermissionCondition> implements Factory<HasPermissionCondition> {
        @Override
        public HasPermissionCondition createCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new HasPermissionCondition(element, simpleMethod);
        }

        @Override
        public HasPermissionCondition createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new HasPermissionCondition(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "if-has-permission";
        }
    }
}
