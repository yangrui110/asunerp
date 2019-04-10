
package com.hanlin.fadp.webtools.artifactinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.hanlin.fadp.base.container.ContainerException;

import com.hanlin.fadp.testtools.*;

/**
 * Event used to run a Junit test
 */
public class RunTestEvents {

    public static final String module = RunTestEvents.class.getName();

    public static String runTest(HttpServletRequest request, HttpServletResponse response) throws ContainerException {
        
        String component = request.getParameter("compName");
        String suiteName = request.getParameter("suiteName");
        String caseName = request.getParameter("caseName");
        String result = null;

        String[] args = null;
        if (caseName == null) {
            args = new String[]{"-component=" + component, " -suitename=" + suiteName + " -loglevel=info"};
        } else {
            args = new String[]{"-component=" + component, " -suitename=" + suiteName, " -case=" + caseName, " -loglevel=info"};
        }

        TestRunContainer testRunContainer = new TestRunContainer();
        testRunContainer.init(args, "frontend test run", "   ");
        if (testRunContainer.start() == false) {
            result = "error";
        } else {
            result = "success";
        }

        return result;
    }
}

