
package com.hanlin.fadp.webapp.webdav;

public interface RequestHandlerFactory {
    /** Returns a <code>RequestHandler<code> instance appropriate
     * for the WebDAV HTTP method.
     *
     * @param method The WebDAV HTTP method. Implementations MUST
     * provide handlers for the following methods: PROPFIND, PROPPATCH,
     * MKCOL, GET, HEAD, POST, DELETE, PUT, COPY, MOVE, LOCK, UNLOCK.
     * @return A <code>RequestHandler</code> instance. Implementations
     * of this interface MUST NOT return <code>null</code>.
     */
    public RequestHandler getHandler(String method);
}
