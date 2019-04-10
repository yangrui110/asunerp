
package com.hanlin.fadp.service.mail;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.LocalDispatcher;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
public class ServiceMcaRule implements java.io.Serializable {

    public static final String module = ServiceMcaRule.class.getName();

    protected String ruleName = null;
    protected List<ServiceMcaCondition> conditions = new LinkedList<ServiceMcaCondition>();
    protected List<ServiceMcaAction> actions = new LinkedList<ServiceMcaAction>();
    protected boolean enabled = true;

    public ServiceMcaRule(Element mca) {
        this.ruleName = mca.getAttribute("mail-rule-name");

        for (Element condElement: UtilXml.childElementList(mca, "condition-field")) {
            conditions.add(new ServiceMcaCondition(condElement, ServiceMcaCondition.CONDITION_FIELD));
        }

        for (Element condElement: UtilXml.childElementList(mca, "condition-header")) {
            conditions.add(new ServiceMcaCondition(condElement, ServiceMcaCondition.CONDITION_HEADER));
        }

        for (Element condElement: UtilXml.childElementList(mca, "condition-service")) {
            conditions.add(new ServiceMcaCondition(condElement, ServiceMcaCondition.CONDITION_SERVICE));
        }

        for (Element actionElement: UtilXml.childElementList(mca, "action")) {
            actions.add(new ServiceMcaAction(actionElement));
        }
    }

    public void eval(LocalDispatcher dispatcher, MimeMessageWrapper messageWrapper, Set<String> actionsRun, GenericValue userLogin) throws GenericServiceException {
        if (!enabled) {
            Debug.logInfo("Service MCA [" + ruleName + "] is disabled; not running.", module);
            return;
        }

        boolean allCondTrue = true;
        for (ServiceMcaCondition cond: conditions) {
            if (!cond.eval(dispatcher, messageWrapper, userLogin)) {
                allCondTrue = false;
                break;
            }
        }

        if (allCondTrue) {
            for (ServiceMcaAction action: actions) {
                if (!actionsRun.contains(action.serviceName)) {
                    if (action.runAction(dispatcher, messageWrapper, userLogin)) {
                        actionsRun.add(action.serviceName);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
