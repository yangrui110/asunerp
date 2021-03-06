
package com.hanlin.fadp.base.conversion;

/** ConverterCreator interface. Classes implement this interface to create a
 * converter that can convert one Java object type to another.
 */
public interface ConverterCreator {
    /** Creates a Converter that can convert the <code>sourceClass</code> to
     * the <code>targetClass</code>. Returns <code>null</code> if this creater
     * doesn't support the class pair.
     *
     * @param sourceClass The source <code>Class</code> to convert
     * @param targetClass The target <code>Class</code> to convert to
     * @return a converter that can convert <code>Object</code>s
     */
    public <S, T> Converter<S, T> createConverter(Class<S> sourceClass, Class<T> targetClass);
}
