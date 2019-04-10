
package com.hanlin.fadp.base.conversion.test;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.conversion.Converter;
import com.hanlin.fadp.base.conversion.ConverterLoader;
import com.hanlin.fadp.base.conversion.Converters;
import com.hanlin.fadp.base.lang.SourceMonitored;
import com.hanlin.fadp.base.test.GenericTestCaseBase;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilMisc;

@SourceMonitored
public class MiscTests extends GenericTestCaseBase {

    public MiscTests(String name) {
        super(name);
    }

    public void testStaticHelperClass() throws Exception {
        assertStaticHelperClass(Converters.class);
    }

    public static class ConverterLoaderImpl implements ConverterLoader {
        public void loadConverters() {
            throw new RuntimeException();
        }
    }

    public void testLoadContainedConvertersIgnoresException() {
        Converters.loadContainedConverters(MiscTests.class);
    }

    public static <S> void assertPassThru(Object wanted, Class<S> sourceClass) throws Exception {
        assertPassThru(wanted, sourceClass, sourceClass);
    }

    public static <S> void assertPassThru(Object wanted, Class<S> sourceClass, Class<? super S> targetClass) throws Exception {
        Converter<S, ? super S> converter = Converters.getConverter(sourceClass, targetClass);
        Object result = converter.convert(UtilGenerics.<S>cast(wanted));
        assertEquals("pass thru convert", wanted, result);
        assertSame("pass thru exact equals", wanted, result);
        assertTrue("pass thru can convert wanted", converter.canConvert(wanted.getClass(), targetClass));
        assertTrue("pass thru can convert source", converter.canConvert(sourceClass, targetClass));
        assertEquals("pass thru source class", wanted.getClass(), converter.getSourceClass());
        assertEquals("pass thru target class", targetClass, converter.getTargetClass());
    }

    public void testPassthru() throws Exception {
        String string = "ofbiz";
        BigDecimal bigDecimal = new BigDecimal("1.234");
        URL url = new URL("http://ofbiz.apache.org");
        List<String> baseList = UtilMisc.toList("a", "1", "b", "2", "c", "3");
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.addAll(baseList);
        List<String> fastList = new LinkedList<String>();
        fastList.addAll(baseList);
        Map<String, String> baseMap = UtilMisc.toMap("a", "1", "b", "2", "c", "3");
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.putAll(baseMap);
        Map<String, String> fastMap = new HashMap<String, String>();
        fastMap.putAll(baseMap);
        Object[] testObjects = new Object[] {
            string,
            bigDecimal,
            url,
            arrayList,
            fastList,
            hashMap,
            fastMap,
        };
        for (Object testObject: testObjects) {
            assertPassThru(testObject, testObject.getClass());
        }
        assertPassThru(fastList, fastList.getClass(), List.class);
        assertPassThru(fastMap, fastMap.getClass(), Map.class);
        assertPassThru(hashMap, hashMap.getClass(), Map.class);
    }
}
