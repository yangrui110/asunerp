
package com.hanlin.fadp.base.conversion;

import java.util.Locale;
import java.util.TimeZone;

/** Abstract LocalizedConverter class. This class handles converter registration
 * and it implements the <code>canConvert</code>, <code>getSourceClass</code>,
 * and <code>getTargetClass</code> methods.
 */
public abstract class AbstractLocalizedConverter<S, T> extends AbstractConverter<S, T> implements LocalizedConverter<S, T> {
    protected AbstractLocalizedConverter(Class<? super S> sourceClass, Class<? super T> targetClass) {
        super(sourceClass, targetClass);
    }

    public T convert(Class<? extends T> targetClass, S obj, Locale locale, TimeZone timeZone) throws ConversionException {
        return convert(obj, locale, timeZone);
    }

    public T convert(Class<? extends T> targetClass, S obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
        return convert(obj, locale, timeZone, formatString);
    }
}
