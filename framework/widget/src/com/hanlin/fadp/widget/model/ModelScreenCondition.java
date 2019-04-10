
package com.hanlin.fadp.widget.model;

import java.util.Map;

import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.widget.model.AbstractModelCondition;
import com.hanlin.fadp.widget.model.AbstractModelCondition.DefaultConditionFactory;
import com.hanlin.fadp.widget.model.ModelCondition;
import com.hanlin.fadp.widget.model.ModelConditionFactory;
import com.hanlin.fadp.widget.model.ModelConditionVisitor;
import com.hanlin.fadp.widget.model.ModelWidget;
import org.w3c.dom.Element;

/**
 * Models the &lt;condition&gt; element.
 * 
 * @see <code>widget-screen.xsd</code>
 */
@SuppressWarnings("serial")
public final class ModelScreenCondition {

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

    public static final String module = ModelScreenCondition.class.getName();
    public static final ModelConditionFactory SCREEN_CONDITION_FACTORY = new ScreenConditionFactory();

    public static class IfEmptySection extends AbstractModelCondition {
        private final FlexibleStringExpander sectionExdr;

        private IfEmptySection(ModelConditionFactory factory, ModelWidget modelWidget, Element condElement) {
            super (factory, modelWidget, condElement);
            this.sectionExdr = FlexibleStringExpander.getInstance(condElement.getAttribute("section-name"));
        }

        @Override
        public void accept(ModelConditionVisitor visitor) throws Exception {
            visitor.visit(this);
        }

        @Override
        public boolean eval(Map<String, Object> context) {
            Map<String, Object> sectionsMap = UtilGenerics.toMap(context.get("sections"));
            return !sectionsMap.containsKey(this.sectionExdr.expandString(context));
        }

        public FlexibleStringExpander getSectionExdr() {
            return sectionExdr;
        }
    }

    private static class ScreenConditionFactory extends DefaultConditionFactory {

        @Override
        public ModelCondition newInstance(ModelWidget modelWidget, Element conditionElement) {
            if (conditionElement == null) {
                return DefaultConditionFactory.TRUE;
            }
            if ("if-empty-section".equals(conditionElement.getNodeName())) {
                return new IfEmptySection(this, modelWidget, conditionElement);
            } else {
                return super.newInstance(this, modelWidget,conditionElement);
            }
        }
    }
}
