
package com.hanlin.fadp.entity;

/**
 * GenericDataSourceException
 *
 */
@SuppressWarnings("serial")
public class GenericDataSourceException extends GenericEntityException {

    public GenericDataSourceException() {
        super();
    }

    public GenericDataSourceException(String str) {
        super(str);
    }

    public GenericDataSourceException(String str, Throwable nested) {
        super(str, nested);
    }
}
