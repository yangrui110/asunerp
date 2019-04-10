
package com.hanlin.fadp.widget.model;

import java.util.Map;

import com.hanlin.fadp.base.util.GeneralException;

public interface ModelAction {

    void accept(ModelActionVisitor visitor) throws Exception;

    /**
     * Executes this action.
     * 
     * @param context
     * @throws GeneralException
     */
    void runAction(Map<String, Object> context) throws GeneralException;
}
