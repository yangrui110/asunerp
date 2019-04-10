
package com.hanlin.fadp.service.test;

import java.util.Map;

import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.testtools.OFBizTestCase;

public class ServiceEngineTests extends OFBizTestCase {

    public ServiceEngineTests(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public void testBasicJavaInvocation() throws Exception {
        Map<String, Object> result = dispatcher.runSync("testScv", UtilMisc.toMap("message", "Unit Test"));
        assertEquals("Service result success", ModelService.RESPOND_SUCCESS, result.get(ModelService.RESPONSE_MESSAGE));
    }
}
