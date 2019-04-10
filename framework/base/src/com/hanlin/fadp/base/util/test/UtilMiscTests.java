
package com.hanlin.fadp.base.util.test;

import java.util.List;
import java.util.Locale;

import com.hanlin.fadp.base.test.GenericTestCaseBase;
import com.hanlin.fadp.base.util.UtilMisc;

public class UtilMiscTests extends GenericTestCaseBase {

    public UtilMiscTests(String name) {
        super(name);
    }

    public void testLocales() throws Exception {
        List<Locale> availableLocales = UtilMisc.availableLocales();
        for (Locale availableLocale : availableLocales) {
            if (availableLocale.getDisplayName().isEmpty()) {
                fail("Locale.getDisplayName() is empty");
            }
        }
    }
}
