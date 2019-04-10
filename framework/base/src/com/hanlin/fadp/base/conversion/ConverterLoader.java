
package com.hanlin.fadp.base.conversion;

/** Converter loader interface. Applications implement this
 * interface to load their Java object converters.
 *
 */
public interface ConverterLoader {
    /** Create and register converters with the Java object type
     * conversion framework. If the converter extends one of the
     * converter abstract classes, then the converter will register
     * itself when an instance is created. Otherwise, call
     * {@link com.hanlin.fadp.base.conversion.Converters#registerConverter(Converter)}
     * with the <code>Converter</code> instance.
     *
     */
    public void loadConverters();
}
