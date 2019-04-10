
package com.hanlin.fadp.minilang.method.ifops.test;

import java.util.Locale;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.service.LocalDispatcher;
import com.hanlin.fadp.service.testtools.OFBizTestCase;

public class IfRegexpTest extends OFBizTestCase {

    private static final String module = IfRegexpTest.class.getName();

    public IfRegexpTest(String name) {
        super(name);
    }

    public void testIfRegexpThreadSafety() throws Exception {
        MyThread t1 = new MyThread(null, dispatcher);
        MyThread t2 = new MyThread(t1, dispatcher);

        t1.start();
        Thread.sleep(1000);
        t2.start();

        try {
            t1.join(15000);
        } catch (InterruptedException e) {
            // if within 15 secs no problem has occurred, assume success
        }

        assertTrue(t1.success);
        assertTrue(t2.success);
    }

    private static class MyThread extends Thread {
        private boolean stopNow = false;
        private boolean success = true;
        private final MyThread friend;
        private final LocalDispatcher dispatcher;

        public MyThread(MyThread friend, LocalDispatcher dispatcher) {
            this.friend = friend;
            this.dispatcher = dispatcher;
        }

        @Override
        public void run() {
            int tryCount = 0;
            while (!stopNow) {
                MethodContext methodContext = new MethodContext(dispatcher.getDispatchContext(), UtilMisc.toMap("locale", Locale.getDefault()), null);
                try {
                    tryCount++;
                    String responseCode = SimpleMethod.runSimpleMethod("component://minilang/script/org/ofbiz/minilang/method/ifops/IfRegexpTests.xml", "testIfRegexp", methodContext);
                    if (!"success".equals(methodContext.getEnv("responseMessage"))) {
                        success = false;
                        Debug.logError("ResponseCode not success: [" + responseCode + "], tryCount: [" + tryCount + "], envMap: [" + methodContext.getEnvMap() + "]", module);
                        if (friend != null) friend.stopNow = true;
                        break;
                    }
                } catch (MiniLangException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
