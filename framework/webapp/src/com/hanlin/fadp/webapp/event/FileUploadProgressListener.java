
package com.hanlin.fadp.webapp.event;

import org.apache.commons.fileupload.ProgressListener;

import java.io.Serializable;

/**
 * FileUploadProgressListener - Commons FileUpload progress listener
 */
@SuppressWarnings("serial")
public class FileUploadProgressListener implements ProgressListener, Serializable {

    public static final String module = FileUploadProgressListener.class.getName();

    protected long contentLength = -1;
    protected long bytesRead = -1;
    protected int items = -1;
    protected boolean hasStarted = false;

    public void update(long bytesRead, long contentLength, int items) {
        this.contentLength = contentLength;
        this.bytesRead = bytesRead;
        this.items = items;
        if (!hasStarted) {
            hasStarted = true;
        }
    }

    public long getContentLength() {
        return contentLength;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public int getItems() {
        return items;
    }

    public boolean hasStarted() {
        return hasStarted;
    }
}
