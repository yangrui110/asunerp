
package com.hanlin.fadp.entity;

import com.hanlin.fadp.base.util.Debug;

/** A <code>DelegatorFactory</code> implementation that returns an
 * instance of <code>GenericDelegator</code>. */
public class DelegatorFactoryImpl extends DelegatorFactory {

    public static final String module = DelegatorFactoryImpl.class.getName();

    public Delegator getInstance(String delegatorName) {
        if (Debug.infoOn()) Debug.logInfo("Creating new delegator [" + delegatorName + "] (" + Thread.currentThread().getName() + ")", module);
        //Debug.logInfo(new Exception(), "Showing stack where new delegator is being created...", module);
        try {
            return new GenericDelegator(delegatorName);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error creating delegator", module);
            return null;
        }
    }
}
