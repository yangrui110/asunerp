
package com.hanlin.fadp.service;

import java.util.List;
import java.util.ArrayList;

import com.hanlin.fadp.base.util.UtilValidate;

/**
 * ServiceValidationException
 */
@SuppressWarnings("serial")
public class ServiceValidationException extends GenericServiceException {

    protected List<String> messages = new ArrayList<String>();
    protected List<String> missingFields = new ArrayList<String>();
    protected List<String> extraFields = new ArrayList<String>();
    protected String errorMode = null;
    protected ModelService service = null;

    public ServiceValidationException(ModelService service, List<String> missingFields, List<String> extraFields, String errorMode) {
        super();
        this.service = service;
        this.errorMode = errorMode;
        if (missingFields != null) {
            this.missingFields = missingFields;
        }
        if (extraFields != null) {
            this.extraFields = extraFields;
        }
    }

    public ServiceValidationException(String str, ModelService service) {
        super(str);
        this.service = service;
    }

    public ServiceValidationException(String str, ModelService service, List<String> missingFields, List<String> extraFields, String errorMode) {
        super(str);
        this.service = service;
        this.errorMode = errorMode;
        if (missingFields != null) {
            this.missingFields = missingFields;
        }
        if (extraFields != null) {
            this.extraFields = extraFields;
        }
    }

    public ServiceValidationException(String str, Throwable nested, ModelService service) {
        super(str, nested);
        this.service = service;
    }

    public ServiceValidationException(String str, Throwable nested, ModelService service, List<String> missingFields, List<String> extraFields, String errorMode) {
        super(str, nested);
        this.service = service;
        this.errorMode = errorMode;
        if (missingFields != null) {
            this.missingFields = missingFields;
        }
        if (extraFields != null) {
            this.extraFields = extraFields;
        }
    }

    public ServiceValidationException(List<String> messages, ModelService service, List<String> missingFields, List<String> extraFields, String errorMode) {
        super();
        this.messages = messages;
        this.service = service;
        this.errorMode = errorMode;
        if (missingFields != null) {
            this.missingFields = missingFields;
        }
        if (extraFields != null) {
            this.extraFields = extraFields;
        }
    }

    public ServiceValidationException(List<String> messages, ModelService service, String errorMode) {
        this(messages, service, null, null, errorMode);
    }

    public List<String> getExtraFields() {
        return extraFields;
    }

    public List<String> getMissingFields() {
        return missingFields;
    }

    @Override
    public List<String> getMessageList() {
        if (UtilValidate.isEmpty(this.messages)) {
            return null;
        }
        return this.messages;
    }

    public ModelService getModelService() {
        return service;
    }

    public String getMode() {
        return errorMode;
    }

    public String getServiceName() {
        if (service != null) {
            return service.name;
        } else {
            return null;
        }
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (UtilValidate.isNotEmpty(this.messages)) {
            StringBuilder sb = new StringBuilder();
            if (msg != null) {
                sb.append(msg).append('\n');
            }
            for (String m: this.messages) {
                sb.append(m);
            }
            msg = sb.toString();
        }
        return msg;
    }
}

