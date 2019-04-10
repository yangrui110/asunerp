
package com.hanlin.fadp.testtools;

import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.TestResult;

import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.testtools.OFBizTestCase;
import org.w3c.dom.Element;

public class ServiceTest extends OFBizTestCase {

    public static final String module = ServiceTest.class.getName();

    protected String serviceName;

    /**
     * Tests of Service
     * @param caseName test case name
     * @param mainElement DOM main element 
     */
    public ServiceTest(String caseName, Element mainElement) {
        super(caseName);
        this.serviceName = mainElement.getAttribute("service-name");
    }

    @Override
    public int countTestCases() {
        return 1;
    }

    @Override
    public void run(TestResult result) {
        result.startTest(this);

        try {

            Map<String, Object> serviceResult = dispatcher.runSync(serviceName, UtilMisc.toMap("test", this, "testResult", result));

            // do something with the errorMessage
            String errorMessage = (String) serviceResult.get(ModelService.ERROR_MESSAGE);
            if (UtilValidate.isNotEmpty(errorMessage)) {
                result.addFailure(this, new AssertionFailedError(errorMessage));
            }

            // do something with the errorMessageList
            List<Object> errorMessageList = UtilGenerics.checkList(serviceResult.get(ModelService.ERROR_MESSAGE_LIST));
            if (UtilValidate.isNotEmpty(errorMessageList)) {
                for (Object message: errorMessageList) {
                    result.addFailure(this, new AssertionFailedError(message.toString()));
                }
            }

            // do something with the errorMessageMap
            Map<String, Object> errorMessageMap = UtilGenerics.cast(serviceResult.get(ModelService.ERROR_MESSAGE_MAP));
            if (!UtilValidate.isEmpty(errorMessageMap)) {
                for (Map.Entry<String, Object> entry: errorMessageMap.entrySet()) {
                    result.addFailure(this, new AssertionFailedError(entry.getKey() + ": " + entry.getValue()));
                }
            }

        } catch (GenericServiceException e) {
            result.addError(this, e);
        }

        result.endTest(this);
    }
}
