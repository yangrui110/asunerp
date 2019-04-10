
package com.hanlin.fadp.entityext.eca;

import java.util.LinkedList;
import java.util.List;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.entity.GenericEntity;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.service.DispatchContext;
import org.w3c.dom.Element;

/**
 * EntityEcaCondition
 */
@SuppressWarnings("serial")
public final class EntityEcaCondition implements java.io.Serializable {

    public static final String module = EntityEcaCondition.class.getName();

    private final String lhsValueName, rhsValueName;
    private final String operator;
    private final String compareType;
    private final String format;
    private final boolean constant;

    public EntityEcaCondition(Element condition, boolean constant) {
        this.lhsValueName = condition.getAttribute("field-name");
        this.constant = constant;
        if (constant) {
            this.rhsValueName = condition.getAttribute("value");
        } else {
            this.rhsValueName = condition.getAttribute("to-field-name");
        }
        this.operator = condition.getAttribute("operator");
        this.compareType = condition.getAttribute("type");
        this.format = condition.getAttribute("format");
    }

    public boolean eval(DispatchContext dctx, GenericEntity value) throws GenericEntityException {
        if (dctx == null || value == null || dctx.getClassLoader() == null) {
            throw new GenericEntityException("Cannot have null Value or DispatchContext!");
        }

        if (Debug.verboseOn()) Debug.logVerbose(this.toString(), module);

        Object lhsValue = value.get(lhsValueName);

        Object rhsValue;
        if (constant) {
            rhsValue = rhsValueName;
        } else {
            rhsValue = value.get(rhsValueName);
        }

        if (Debug.verboseOn()) Debug.logVerbose("Comparing : " + lhsValue + " " + operator + " " + rhsValue, module);

        // evaluate the condition & invoke the action(s)
        List<Object> messages = new LinkedList<Object>();
        Boolean cond = ObjectType.doRealCompare(lhsValue, rhsValue, operator, compareType, format, messages, null, dctx.getClassLoader(), constant);

        // if any messages were returned send them out
        if (messages.size() > 0) {
            for (Object message: messages) {
                Debug.logWarning((String) message, module);
            }
        }
        if (cond != null) {
            return cond.booleanValue();
        } else {
            return false;
        }
    }

    public String getLValue() {
        return this.lhsValueName;
    }

    public String getRValue() {
        if (constant && !rhsValueName.isEmpty()) {
            return "\"".concat(this.rhsValueName).concat("\"");
        }
        return this.rhsValueName;
    }

    public String getOperator() {
        return this.operator;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[").append(lhsValueName).append("]");
        buf.append("[").append(operator).append("]");
        buf.append("[").append(rhsValueName).append("]");
        buf.append("[").append(constant).append("]");
        buf.append("[").append(compareType).append("]");
        buf.append("[").append(format).append("]");
        return buf.toString();
    }
}
