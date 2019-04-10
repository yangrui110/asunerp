
package com.hanlin.fadp.testtools;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.TestResult;

import com.hanlin.fadp.base.location.FlexibleLocation;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.util.EntityDataAssert;
import com.hanlin.fadp.entity.util.EntitySaxReader;
import com.hanlin.fadp.service.testtools.OFBizTestCase;
import org.w3c.dom.Element;

public class EntityXmlAssertTest extends OFBizTestCase {

    public static final String module = ServiceTest.class.getName();

    protected String entityXmlUrlString;
    protected String action;

    /**
     * Tests of entity xml
     * @param caseName test case name
     * @param mainElement DOM main element 
     */
    public EntityXmlAssertTest(String caseName, Element mainElement) {
        super(caseName);
        this.entityXmlUrlString = mainElement.getAttribute("entity-xml-url");
        this.action = mainElement.getAttribute("action");
        if (UtilValidate.isEmpty(this.action)) this.action = "assert";
    }

    @Override
    public int countTestCases() {
        int testCaseCount = 0;
        try {
            URL entityXmlURL = FlexibleLocation.resolveLocation(entityXmlUrlString);
            EntitySaxReader reader = new EntitySaxReader(delegator);
            testCaseCount += reader.parse(entityXmlURL);
        } catch (Exception e) {
            Debug.logError(e, "Error getting test case count", module);
        }
        return testCaseCount;
    }

    @Override
    public void run(TestResult result) {
        result.startTest(this);

        try {
            URL entityXmlURL = FlexibleLocation.resolveLocation(entityXmlUrlString);
            List<Object> errorMessages = new LinkedList<Object>();

            if ("assert".equals(this.action)) {
                EntityDataAssert.assertData(entityXmlURL, delegator, errorMessages);
            } else if ("load".equals(this.action)) {
                EntitySaxReader reader = new EntitySaxReader(delegator);
                reader.parse(entityXmlURL);
            } else {
                // uh oh, bad value
                result.addFailure(this, new AssertionFailedError("Bad value [" + this.action + "] for action attribute of entity-xml element"));
            }

            if (UtilValidate.isNotEmpty(errorMessages)) {
                for (Object failureMessage: errorMessages) {
                    result.addFailure(this, new AssertionFailedError(failureMessage.toString()));
                }
            }
        } catch (Exception e) {
            result.addError(this, e);
        }

        result.endTest(this);
    }
}
