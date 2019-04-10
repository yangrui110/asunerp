

package com.hanlin.fadp.service;

/**
 * ModelServiceIface
 */
public class ModelServiceIface {

    protected String service;
    protected boolean optional;

    public ModelServiceIface(String service, boolean optional) {
        this.service = service;
        this.optional = optional;
    }

    public String getService() {
        return this.service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public void isOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    public String toString() {
        return "[" + service + ":" + optional + "]";
    }
}
