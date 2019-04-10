
package com.hanlin.fadp.widget.model;

import org.w3c.dom.Element;

/**
 * A factory for <code>Condition</code> instances.
 *
 */
public interface ModelConditionFactory {
    /**
     * Returns a new <code>ModelCondition</code> instance built from <code>conditionElement</code>.
     * 
     * @param modelWidget The <code>ModelWidget</code> that contains the <code>Condition</code> instance.
     * @param conditionElement The XML element used to build the <code>Condition</code> instance.
     * @return A new <code>ModelCondition</code> instance built from <code>conditionElement</code>.
     * @throws IllegalArgumentException if no model was found for the XML element
     */
    ModelCondition newInstance(ModelWidget modelWidget, Element conditionElement);
}
