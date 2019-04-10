
package com.hanlin.fadp.base.config;

import java.net.URL;
import java.io.InputStream;
import com.hanlin.fadp.base.util.UtilURL;

/**
 * Loads resources from the file system
 *
 */
@SuppressWarnings("serial")
public class FileLoader extends ResourceLoader implements java.io.Serializable {

    @Override
    public URL getURL(String location) throws GenericConfigException {
        String fullLocation = fullLocation(location);
        URL fileUrl = null;

        fileUrl = UtilURL.fromFilename(fullLocation);
        if (fileUrl == null) {
            throw new GenericConfigException("File Resource not found: " + fullLocation);
        }
        return fileUrl;
    }

    @Override
    public InputStream loadResource(String location) throws GenericConfigException {
        URL fileUrl = getURL(location);
        try {
            return fileUrl.openStream();
        } catch (java.io.IOException e) {
            throw new GenericConfigException("Error opening file at location [" + fileUrl.toExternalForm() + "]", e);
        }
    }
}
