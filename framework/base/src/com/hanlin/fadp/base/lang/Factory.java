
package com.hanlin.fadp.base.lang;

/** Factory interface. */
public interface Factory<R, A> {

    /** Returns an instance of <code>R</code>. This is a basic factory interface
     * that is meant to be extended. Sub-interfaces declare types for
     * <code>A</code> (the <code>getInstance</code> argument), and
     * <code>R</code> (the type returned by <code>getInstance</code>).
     *
     * @param obj Optional object to be used in <code>R</code>'s construction,
     * or to be used as a selector key
     * @return An instance of <code>R</code>
     */
    public R getInstance(A obj);

}
