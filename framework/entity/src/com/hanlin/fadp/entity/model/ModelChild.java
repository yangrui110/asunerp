
package com.hanlin.fadp.entity.model;

import java.io.Serializable;

/**
 * Abstract entity model class.
 *
 */
@SuppressWarnings("serial")
public abstract class ModelChild implements Serializable {

    private final ModelEntity modelEntity;
    /** The description for documentation purposes */
    private final String description;

    // TODO: Eliminate the need for this.
    protected ModelChild() {
        this.modelEntity = null;
        this.description = "";
    }

    protected ModelChild(ModelEntity modelEntity, String description) {
        this.modelEntity = modelEntity;
        this.description = description;
    }

    public ModelEntity getModelEntity() {
        return this.modelEntity;
    }

    /** The description for documentation purposes */
    public String getDescription() {
        return this.description;
    }
}
