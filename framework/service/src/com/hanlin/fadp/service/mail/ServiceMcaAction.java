
package com.hanlin.fadp.service.mail;

import java.util.HashMap;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.LocalDispatcher;
import com.hanlin.fadp.service.ServiceUtil;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
public class ServiceMcaAction implements java.io.Serializable {

    public static final String module = ServiceMcaAction.class.getName();

    protected String serviceName = null;
    protected String serviceMode = null;
    protected String runAsUser = null;
    protected boolean persist = false;

    protected ServiceMcaAction() { }

    protected ServiceMcaAction(Element action) {
        this.serviceName = action.getAttribute("service");
        this.serviceMode = action.getAttribute("mode");
        this.runAsUser = action.getAttribute("run-as-user");
        // support the old, inconsistent attribute name
        if (UtilValidate.isEmail(this.runAsUser)) this.runAsUser = action.getAttribute("runAsUser");
        this.persist = "true".equals(action.getAttribute("persist"));
    }

    public boolean runAction(LocalDispatcher dispatcher, MimeMessageWrapper messageWrapper, GenericValue userLogin) throws GenericServiceException {
        Map<String, Object> serviceContext = new HashMap<String, Object>();
        serviceContext.putAll(UtilMisc.toMap("messageWrapper", messageWrapper, "userLogin", userLogin));
        serviceContext.put("userLogin", ServiceUtil.getUserLogin(dispatcher.getDispatchContext(), serviceContext, runAsUser));

        if (serviceMode.equals("sync")) {
            Map<String, Object> result = dispatcher.runSync(serviceName, serviceContext);
            if (ServiceUtil.isError(result)) {
                Debug.logError(ServiceUtil.getErrorMessage(result), module);
                return false;
            } else {
                return true;
            }
        } else if (serviceMode.equals("async")) {
            dispatcher.runAsync(serviceName, serviceContext, persist);
            return true;
        } else {
            Debug.logError("Invalid service mode [" + serviceMode + "] unable to invoke MCA action (" + serviceName + ").", module);
            return false;
        }
    }
}
