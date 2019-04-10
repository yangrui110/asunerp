
package com.hanlin.fadp.entity;

import java.util.Map;

import com.hanlin.fadp.entity.model.ModelEntity;

/**
 * Generic Entity Primary Key Object
 *
 */
@SuppressWarnings("serial")
public class GenericPK extends GenericEntity {

    protected GenericPK() { }

    /** Creates new GenericPK */
    public static GenericPK create(ModelEntity modelEntity) {
        GenericPK newPK = new GenericPK();
        newPK.init(modelEntity);
        return newPK;
    }

    /** Creates new GenericPK from existing Map */
    public static GenericPK create(Delegator delegator, ModelEntity modelEntity, Map<String, ? extends Object> fields) {
        GenericPK newPK = new GenericPK();
       
        newPK.init(delegator, modelEntity, fields);
        return newPK;
    }

    /** Creates new GenericPK from existing Map */
    public static GenericPK create(Delegator delegator, ModelEntity modelEntity, Object singlePkValue) {
        GenericPK newPK = new GenericPK();
        newPK.init(delegator, modelEntity, singlePkValue);
        return newPK;
    }

    /** Creates new GenericPK from existing GenericPK */
    public static GenericPK create(GenericPK value) {
        GenericPK newPK = new GenericPK();
        newPK.init(value);
        return newPK;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GenericPK) {
            return super.equals(obj);
        }
        return false;
    }

    /** Clones this GenericPK, this is a shallow clone & uses the default shallow HashMap clone
     *@return Object that is a clone of this GenericPK
     */
    @Override
    public Object clone() {
        return GenericPK.create(this);
    }
}
