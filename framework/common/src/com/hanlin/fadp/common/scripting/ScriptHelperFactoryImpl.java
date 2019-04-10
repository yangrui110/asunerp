
package com.hanlin.fadp.common.scripting;

import javax.script.ScriptContext;

import com.hanlin.fadp.base.util.ScriptHelper;
import com.hanlin.fadp.base.util.ScriptHelperFactory;

/**
 * An implementation of the <code>ScriptHelperFactory</code> interface.
 */
public final class ScriptHelperFactoryImpl implements ScriptHelperFactory {

    @Override
    public ScriptHelper getInstance(ScriptContext context) {
        return new ScriptHelperImpl(context);
    }
}
