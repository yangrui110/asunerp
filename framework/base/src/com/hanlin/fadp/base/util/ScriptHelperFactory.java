
package com.hanlin.fadp.base.util;

import javax.script.ScriptContext;

import com.hanlin.fadp.base.lang.Factory;

/**
 * A <code>ScriptHelper</code> factory.
 */
public interface ScriptHelperFactory extends Factory<ScriptHelper, ScriptContext> {

    ScriptHelper getInstance(ScriptContext context);
}
