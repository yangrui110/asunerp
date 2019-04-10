
package com.hanlin.fadp.datafile;

import com.hanlin.fadp.base.util.GeneralException;

/**
 * DataFileException
 */
@SuppressWarnings("serial")
public class DataFileException extends GeneralException {

    public DataFileException() {
        super();
    }

    public DataFileException(String str) {
        super(str);
    }

    public DataFileException(String str, Throwable nested) {
        super(str, nested);
    }
}
