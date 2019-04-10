
package com.hanlin.fadp.widget.model;

import org.w3c.dom.Element;

/**
 * Models the &lt;condition&gt; element.
 * 
 * @see <code>widget-tree.xsd</code>
 */
public class ModelTreeCondition {
    public static final String module = ModelTreeCondition.class.getName();
    private final ModelCondition condition;

    public ModelTreeCondition(ModelTree modelTree, Element conditionElement) {
        this.condition = AbstractModelCondition.DEFAULT_CONDITION_FACTORY.newInstance(modelTree, conditionElement);
    }

    public ModelCondition getCondition() {
        return condition;
    }
}
