
package com.hanlin.fadp.base.util;

/**
 * HttpClient Exception.
 *
 */
@SuppressWarnings("serial")
public class HttpClientException extends GeneralException {

    Throwable nested = null;

    public HttpClientException() {
        super();
    }

    public HttpClientException(String str) {
        super(str);
    }

    public HttpClientException(String str, Throwable nested) {
        super(str, nested);
    }
}

