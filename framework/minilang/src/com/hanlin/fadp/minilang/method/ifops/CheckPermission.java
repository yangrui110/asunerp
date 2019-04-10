
package com.hanlin.fadp.minilang.method.ifops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MessageElement;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import com.hanlin.fadp.security.Security;
import org.w3c.dom.Element;

/**
 * Implements the &lt;check-permission&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Ccheckpermission%3E}}">Mini-language Reference</a>
 */
public final class CheckPermission extends MethodOperation {

    private final List<PermissionInfo> altPermissionInfoList;
    private final FlexibleMapAccessor<List<String>> errorListFma;
    private final MessageElement messageElement;
    private final PermissionInfo primaryPermissionInfo;

    public CheckPermission(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "permission", "action", "error-list-name");
            MiniLangValidate.constantAttributes(simpleMethod, element, "error-list-name");
            MiniLangValidate.childElements(simpleMethod, element, "alt-permission", "fail-message", "fail-property");
            MiniLangValidate.requireAnyChildElement(simpleMethod, element, "fail-message", "fail-property");
        }
        errorListFma = FlexibleMapAccessor.getInstance(MiniLangValidate.checkAttribute(element.getAttribute("error-list-name"), "error_list"));
        primaryPermissionInfo = new PermissionInfo(element);
        List<? extends Element> altPermElements = UtilXml.childElementList(element, "alt-permission");
        if (!altPermElements.isEmpty()) {
            List<PermissionInfo> permissionInfoList = new ArrayList<PermissionInfo>(altPermElements.size());
            for (Element altPermElement : altPermElements) {
                permissionInfoList.add(new PermissionInfo(altPermElement));
            }
            altPermissionInfoList = Collections.unmodifiableList(permissionInfoList);
        } else {
            altPermissionInfoList = null;
        }
        messageElement = MessageElement.fromParentElement(element, simpleMethod);
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        boolean hasPermission = false;
        GenericValue userLogin = methodContext.getUserLogin();
        if (userLogin != null) {
            Security security = methodContext.getSecurity();
            hasPermission = this.primaryPermissionInfo.hasPermission(methodContext, userLogin, security);
            if (!hasPermission && altPermissionInfoList != null) {
                for (PermissionInfo altPermInfo : altPermissionInfoList) {
                    if (altPermInfo.hasPermission(methodContext, userLogin, security)) {
                        hasPermission = true;
                        break;
                    }
                }
            }
        }
        if (!hasPermission && messageElement != null) {
            List<String> messages = errorListFma.get(methodContext.getEnvMap());
            if (messages == null) {
                messages = new LinkedList<String>();
                errorListFma.put(methodContext.getEnvMap(), messages);
            }
            messages.add(messageElement.getMessage(methodContext));
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<check-permission ");
        sb.append("permission=\"").append(this.primaryPermissionInfo.permissionFse).append("\" ");
        if (!this.primaryPermissionInfo.actionFse.isEmpty()) {
            sb.append("action=\"").append(this.primaryPermissionInfo.actionFse).append("\" ");
        }
        if (!"error_list".equals(this.errorListFma.getOriginalName())) {
            sb.append("error-list-name=\"").append(this.errorListFma).append("\" ");
        }
        if (messageElement != null) {
            sb.append(">").append(messageElement).append("</check-permission>");
        } else {
            sb.append("/>");
        }
        return sb.toString();
    }

    /**
     * A &lt;check-permission&gt; element factory. 
     */
    public static final class CheckPermissionFactory implements Factory<CheckPermission> {
        @Override
        public CheckPermission createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CheckPermission(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "check-permission";
        }
    }

    private class PermissionInfo {
        private final FlexibleStringExpander actionFse;
        private final FlexibleStringExpander permissionFse;

        private PermissionInfo(Element element) throws MiniLangException {
            if (MiniLangValidate.validationOn()) {
                MiniLangValidate.attributeNames(simpleMethod, element, "permission", "action");
                MiniLangValidate.requiredAttributes(simpleMethod, element, "permission");
            }
            this.permissionFse = FlexibleStringExpander.getInstance(element.getAttribute("permission"));
            this.actionFse = FlexibleStringExpander.getInstance(element.getAttribute("action"));
        }

        private boolean hasPermission(MethodContext methodContext, GenericValue userLogin, Security security) {
            String permission = permissionFse.expandString(methodContext.getEnvMap());
            String action = actionFse.expandString(methodContext.getEnvMap());
            if (!action.isEmpty()) {
                // run hasEntityPermission
                return security.hasEntityPermission(permission, action, userLogin);
            } else {
                // run hasPermission
                return security.hasPermission(permission, userLogin);
            }
        }
    }
}
