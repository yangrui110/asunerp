package com.hanlin.fadp.common;

import com.hanlin.fadp.entity.Delegator;

public class UtilCodeStr {
    /**
     * 获取编号
     *
     * @param delegator
     * @param entityName
     * @return
     */
    public static String getCode(Delegator delegator, String entityName) {
        // FIXME: 这里需要改成根据编码规则生成编号
        return delegator.getNextSeqId(entityName + "Code");
    }
}
