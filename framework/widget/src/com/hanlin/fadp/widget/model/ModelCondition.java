
package com.hanlin.fadp.widget.model;

import java.util.Map;

public interface ModelCondition {

    void accept(ModelConditionVisitor visitor) throws Exception;

    boolean eval(Map<String, Object> context);
}
