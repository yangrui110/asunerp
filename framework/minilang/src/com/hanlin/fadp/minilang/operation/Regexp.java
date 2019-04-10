
package com.hanlin.fadp.minilang.operation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.base.util.PatternFactory;
import org.w3c.dom.Element;

/**
 * Validates the current field using a regular expression
 */
public class Regexp extends SimpleMapOperation {

    public static final String module = Regexp.class.getName();
    private Pattern pattern = null;
    String expr;

    public Regexp(Element element, SimpleMapProcess simpleMapProcess) {
        super(element, simpleMapProcess);
        expr = element.getAttribute("expr");
        try {
            pattern = PatternFactory.createOrGetPerl5CompiledPattern(expr, true);
        } catch (MalformedPatternException e) {
            Debug.logError(e, module);
        }
    }

    @Override
    public void exec(Map<String, Object> inMap, Map<String, Object> results, List<Object> messages, Locale locale, ClassLoader loader) {
        Object obj = inMap.get(fieldName);
        String fieldValue = null;
        try {
            fieldValue = (String) ObjectType.simpleTypeConvert(obj, "String", null, locale);
        } catch (GeneralException e) {
            messages.add("Could not convert field value for comparison: " + e.getMessage());
            return;
        }
        if (pattern == null) {
            messages.add("Could not compile regular expression \"" + expr + "\" for validation");
            return;
        }
        PatternMatcher matcher = new Perl5Matcher();
        if (!matcher.matches(fieldValue, pattern)) {
            addMessage(messages, loader, locale);
        }
    }
}
