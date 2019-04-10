

package com.hanlin.fadp.entity.testtools;

import junit.framework.TestCase;

import com.hanlin.fadp.entity.Delegator;

public class EntityTestCase extends TestCase {

    protected Delegator delegator = null;

    public EntityTestCase(String name) {
        super(name);
    }

    public void setDelegator(Delegator delegator) {
        this.delegator = delegator;
    }

    public Delegator getDelegator() {
        return delegator;
    }
}
