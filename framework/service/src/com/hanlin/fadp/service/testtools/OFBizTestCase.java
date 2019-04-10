

package com.hanlin.fadp.service.testtools;

import com.hanlin.fadp.entity.testtools.EntityTestCase;
import com.hanlin.fadp.service.LocalDispatcher;

public class OFBizTestCase extends EntityTestCase {

    protected LocalDispatcher dispatcher = null;

    public LocalDispatcher getDispatcher() {
        return dispatcher;
    }

    public OFBizTestCase(String name) {
        super(name);
    }

    public void setDispatcher(LocalDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}
