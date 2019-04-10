/*******************************************************************************
 * 
 *******************************************************************************/
package com.hanlin.fadp.service;

import com.hanlin.fadp.entity.Delegator;

/**
 * A {@link LocalDispatcher} factory. Implementations register themselves in the fadp-component.xml file.
 */
public interface LocalDispatcherFactory {
    /**
     * Creates a <code>LocalDispatcher</code> instance based on <code>name</code> and <code>delegator</code>.
     * If a matching <code>LocalDispatcher</code> was already created, then it will be returned.
     * 
     * @param name
     * @param delegator
     */
    public LocalDispatcher createLocalDispatcher(String name, Delegator delegator);
}
