
package com.hanlin.fadp.base.util.test;

import java.util.HashSet;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import com.hanlin.fadp.base.lang.SourceMonitored;
import com.hanlin.fadp.base.test.GenericTestCaseBase;
import com.hanlin.fadp.base.util.ReferenceCleaner;

@SourceMonitored
public class ReferenceCleanerTests extends GenericTestCaseBase {
    public ReferenceCleanerTests(String name) {
        super(name);
    }

    public void testReferenceCleaner() throws Exception {
        assertStaticHelperClass(ReferenceCleaner.class);
        final SynchronousQueue<String> queue = new SynchronousQueue<String>();
        Object obj = new Object();
        ReferenceCleaner.Soft<Object> soft = new ReferenceCleaner.Soft<Object>(obj) {
            public void remove() throws Exception {
                queue.put("soft");
                Thread.currentThread().interrupt();
            }
        };
        ReferenceCleaner.Weak<Object> weak = new ReferenceCleaner.Weak<Object>(obj) {
            public void remove() throws Exception {
                queue.put("weak");
                throw new RuntimeException();
            }
        };
        new ReferenceCleaner.Phantom<Object>(obj) {
            public void remove() throws Exception {
                queue.put("phantom");
            }
        };
        HashSet<String> foundEvents = new HashSet<String>();
        useAllMemory();
        assertSame("still-soft", obj, soft.get());
        assertSame("still-weak", obj, weak.get());
        assertNull("no event", queue.poll(100, TimeUnit.MILLISECONDS));
        obj = null;
        useAllMemory();
        foundEvents.add(queue.poll(100, TimeUnit.MILLISECONDS));
        foundEvents.add(queue.poll(100, TimeUnit.MILLISECONDS));
        foundEvents.add(queue.poll(100, TimeUnit.MILLISECONDS));
        useAllMemory();
        foundEvents.add(queue.poll(100, TimeUnit.MILLISECONDS));
        foundEvents.remove(null);
        assertFalse("no null", foundEvents.contains(null));
        assertNull("no-soft", soft.get());
        assertNull("no-weak", weak.get());
        assertTrue("soft event", foundEvents.contains("soft"));
        assertTrue("weak event", foundEvents.contains("weak"));
        assertTrue("phantom event", foundEvents.contains("phantom"));
    }
}
