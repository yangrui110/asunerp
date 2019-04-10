
package com.hanlin.fadp.entity;

/**
 * Thrown when there is a problem with the entity engine configuration.
 *
 * @see <code>entity-config.xsd</code>
 */
@SuppressWarnings("serial")
public class GenericEntityConfException extends GenericEntityException {

    public GenericEntityConfException() {
        super();
    }

    public GenericEntityConfException(String str) {
        super(str);
    }

    public GenericEntityConfException(String str, Throwable nested) {
        super(str, nested);
    }
}
