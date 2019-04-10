
package com.hanlin.fadp.widget.model;

import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import org.w3c.dom.Element;

/**
 * Models the &lt;condition&gt; element.
 * 
 * @see <code>widget-menu.xsd</code>
 */
public final class ModelMenuCondition {

    /*
     * ----------------------------------------------------------------------- *
     *                     DEVELOPERS PLEASE READ
     * ----------------------------------------------------------------------- *
     * 
     * This model is intended to be a read-only data structure that represents
     * an XML element. Outside of object construction, the class should not
     * have any behaviors.
     * 
     * Instances of this class will be shared by multiple threads - therefore
     * it is immutable. DO NOT CHANGE THE OBJECT'S STATE AT RUN TIME!
     * 
     */

    public static final String module = ModelMenuCondition.class.getName();

    private final FlexibleStringExpander passStyleExdr;
    private final FlexibleStringExpander failStyleExdr;
    private final ModelCondition condition;

    public ModelMenuCondition(ModelMenuItem modelMenuItem, Element conditionElement) {
        this.passStyleExdr = FlexibleStringExpander.getInstance(conditionElement.getAttribute("pass-style"));
        this.failStyleExdr = FlexibleStringExpander.getInstance(conditionElement.getAttribute("disabled-style"));
        this.condition = AbstractModelCondition.DEFAULT_CONDITION_FACTORY.newInstance(modelMenuItem, conditionElement);
    }

    public ModelCondition getCondition() {
        return condition;
    }

    public FlexibleStringExpander getFailStyleExdr() {
        return failStyleExdr;
    }

    public FlexibleStringExpander getPassStyleExdr() {
        return passStyleExdr;
    }
}
