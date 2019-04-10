
package com.hanlin.fadp.minilang.method.conditional;

import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.method.MethodContext;

/**
 * Interface for all conditional elements under the &lt;if&gt; element.
 */
public interface Conditional {

    public boolean checkCondition(MethodContext methodContext) throws MiniLangException;

    public void prettyPrint(StringBuilder messageBuffer, MethodContext methodContext);
}
