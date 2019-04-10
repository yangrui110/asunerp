
package com.hanlin.fadp.testtools;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.TestResult;

import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.security.Security;
import com.hanlin.fadp.security.SecurityConfigurationException;
import com.hanlin.fadp.security.SecurityFactory;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.testtools.OFBizTestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import org.w3c.dom.Element;

public class SimpleMethodTest extends OFBizTestCase {

    public static final String module = ServiceTest.class.getName();

    protected String methodLocation;
    protected String methodName;
    
    public static MockHttpServletRequest request = new MockHttpServletRequest();
    public static MockHttpServletResponse response = new MockHttpServletResponse();

    /**
     * Tests of Simple Method
     * @param caseName test case name
     * @param mainElement DOM main element 
     */
    public SimpleMethodTest(String caseName, Element mainElement) {
        this(caseName, mainElement.getAttribute("location"), mainElement.getAttribute("name"));
    }

    public SimpleMethodTest(String caseName, String methodLocation, String methodName) {
        super(caseName);
        this.methodLocation = methodLocation;
        this.methodName = methodName;
    }

    @Override
    public int countTestCases() {
        return 1;
    }

    @Override
    public void run(TestResult result) {
        result.startTest(this);
        
        try {
            // define request
            Security security = SecurityFactory.getInstance(delegator);
            MockServletContext servletContext = new MockServletContext();
            request.setAttribute("security", security);
            request.setAttribute("servletContext", servletContext);
            request.setAttribute("delegator", delegator);
            request.setAttribute("dispatcher", dispatcher);
            Map<String, Object> serviceResult = SimpleMethod.runSimpleService(methodLocation, methodName, dispatcher.getDispatchContext(),
                    UtilMisc.toMap("test", this, "testResult", result, "locale", Locale.getDefault(), "request", request, "response", response));

            // do something with the errorMessage
            String errorMessage = (String) serviceResult.get(ModelService.ERROR_MESSAGE);
            if (UtilValidate.isNotEmpty(errorMessage)) {
                result.addFailure(this, new AssertionFailedError(errorMessage));
            }

            // do something with the errorMessageList
            List<Object> errorMessageList = UtilGenerics.cast(serviceResult.get(ModelService.ERROR_MESSAGE_LIST));
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

        } catch (MiniLangException e) {
            result.addError(this, e);
        } catch (SecurityConfigurationException e) {
            result.addError(this, e);
        }

        result.endTest(this);
    }
}
