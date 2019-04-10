
package com.hanlin.fadp.webapp.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.HttpClient;

/**
 * Test Events
 */
public class TestEvent {

    public static final String module = TestEvent.class.getName();

    public static String test(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("MESSAGE", "Test Event Ran Fine.");
        Debug.logInfo("Test Event Ran Fine.", module);
        return "success";
    }

    public static String httpClientTest(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpClient http = new HttpClient("http://ofbiz.apache.org/cgi-bin/http_test.pl");

            http.setHeader("Cookie", "name=value,value=name");
            http.setHeader("User-Agent", "Mozilla/4.0");
            http.setParameter("testId", "testing");
            Debug.logInfo(http.post(), module);
        } catch (Exception e) {
            Debug.logInfo(e, "HttpClientException Caught.", module);
        }
        return "success";
    }
}
