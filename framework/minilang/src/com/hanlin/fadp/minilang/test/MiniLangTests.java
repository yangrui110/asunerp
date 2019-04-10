
package com.hanlin.fadp.minilang.test;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.service.testtools.OFBizTestCase;

public class MiniLangTests extends OFBizTestCase {

    private static final String module = MiniLangTests.class.getName();

    private final boolean traceEnabled;

    public MiniLangTests(String name) {
        super(name);
        traceEnabled = "true".equals(UtilProperties.getPropertyValue("minilang.properties", "unit.tests.trace.enabled"));
    }

    private Map<String, Object> createContext() {
        return UtilMisc.toMap("locale", Locale.US, "timeZone", TimeZone.getTimeZone("GMT"));
    }

    private MethodContext createServiceMethodContext() {
        MethodContext context = new MethodContext(dispatcher.getDispatchContext(), createContext(), null);
        context.setUserLogin(dispatcher.getDelegator().makeValidValue("UserLogin", UtilMisc.toMap("userLoginId", "system")), "userLogin");
        if (traceEnabled) {
            context.setTraceOn(Debug.INFO);
        }
        return context;
    }

    private SimpleMethod createSimpleMethod(String xmlString) throws Exception {
        return new SimpleMethod(UtilXml.readXmlDocument(xmlString).getDocumentElement(), module);
    }

    public void testAssignmentOperators() throws Exception {
        // <check-errors> and <add-error> tests
        SimpleMethod methodToTest = createSimpleMethod("<simple-method name=\"testCheckErrors\"><check-errors/></simple-method>");
        MethodContext context = createServiceMethodContext();
        String result = methodToTest.exec(context);
        assertEquals("<check-errors> success result", methodToTest.getDefaultSuccessCode(), result);
        List<String> messages = context.getEnv(methodToTest.getServiceErrorMessageListName());
        assertNull("<check-errors> null error message list", messages);
        methodToTest = createSimpleMethod("<simple-method name=\"testCheckErrors\"><add-error><fail-message message=\"This should fail\"/></add-error><check-errors/></simple-method>");
        context = createServiceMethodContext();
        result = methodToTest.exec(context);
        assertEquals("<check-errors> error result", methodToTest.getDefaultErrorCode(), result);
        messages = context.getEnv(methodToTest.getServiceErrorMessageListName());
        assertNotNull("<check-errors> error message list", messages);
        assertTrue("<check-errors> error message text", messages.contains("This should fail"));
        // <assert>, <not>,  and <if-empty> tests
        methodToTest = createSimpleMethod("<simple-method name=\"testAssert\"><assert><not><if-empty field=\"locale\"/></not></assert><check-errors/></simple-method>");
        context = createServiceMethodContext();
        result = methodToTest.exec(context);
        assertEquals("<assert> success result", methodToTest.getDefaultSuccessCode(), result);
        messages = context.getEnv(methodToTest.getServiceErrorMessageListName());
        assertNull("<assert> null error message list", messages);
        methodToTest = createSimpleMethod("<simple-method name=\"testAssert\"><assert><if-empty field=\"locale\"/></assert><check-errors/></simple-method>");
        context = createServiceMethodContext();
        result = methodToTest.exec(context);
        assertEquals("<assert> error result", methodToTest.getDefaultErrorCode(), result);
        messages = context.getEnv(methodToTest.getServiceErrorMessageListName());
        assertNotNull("<assert> error message list", messages);
        String errorMessage = messages.get(0);
        assertTrue("<assert> error message text", errorMessage.startsWith("Assertion failed:"));
    }

    public void testFieldToResultOperation() throws Exception {
        String simpleMethodXml = "<simple-method name=\"testFieldToResult\">" +
                                 "  <set field=\"resultValue\" value=\"someResultValue\"/>" +
                                 "  <set field=\"result1\" value=\"dynamicResultName\"/>" +
                                 "  <field-to-result field=\"resultValue\" result-name=\"constantResultName\"/>" +
                                 "  <field-to-result field=\"resultValue\" result-name=\"${result1}\"/>" +
                                 "</simple-method>";
        SimpleMethod methodToTest = createSimpleMethod(simpleMethodXml);
        MethodContext context = createServiceMethodContext();
        String result = methodToTest.exec(context);
        assertEquals("testFieldToResult success result", methodToTest.getDefaultSuccessCode(), result);
        assertEquals("Plain expression result name set", "someResultValue", context.getResult("constantResultName"));
        assertEquals("Nested expression result name set", "someResultValue", context.getResult("dynamicResultName"));
    }
}
