
package com.hanlin.fadp.service;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.util.UtilValidate;

/**
 * Service Permission Group Model Class
 */
@SuppressWarnings("serial")
public class ModelPermGroup implements Serializable {

    public static final String module = ModelPermGroup.class.getName();

    public static final String PERM_JOIN_AND = "AND";
    public static final String PERM_JOIN_OR = "OR";

    public List<ModelPermission> permissions = new LinkedList<ModelPermission>();
    public String joinType;

    public boolean evalPermissions(DispatchContext dctx, Map<String, ? extends Object> context) {
        if (UtilValidate.isNotEmpty(permissions))  {
            boolean foundOne = false;
            for (ModelPermission perm: permissions) {
                if (perm.evalPermission(dctx, context)) {
                    foundOne = true;
                } else {
                    if (joinType.equals(PERM_JOIN_AND)) {
                        return false;
                    }
                }
            }
            return foundOne;
        } else {
            return true;
        }
    }
}
