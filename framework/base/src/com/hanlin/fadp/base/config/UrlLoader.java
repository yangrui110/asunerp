
package com.hanlin.fadp.base.config;

import java.net.URL;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Loads resources from a URL
 *
 */
@SuppressWarnings("serial")
public class UrlLoader extends ResourceLoader implements Serializable {

    @Override
    public URL getURL(String location) throws GenericConfigException {
        String fullLocation = fullLocation(location);

        URL url = null;

        try {
            url = new URL(fullLocation);
        } catch (java.net.MalformedURLException e) {
            throw new GenericConfigException("Error with malformed URL while trying to load URL resource at location [" + fullLocation + "]", e);
        }
        return url;
    }

    @Override
    public InputStream loadResource(String location) throws GenericConfigException {
        URL url = getURL(location);

        try {
            return url.openStream();
        } catch (java.io.IOException e) {
            throw new GenericConfigException("Error opening URL resource at location [" + url.toExternalForm() + "]", e);
        }
    }
}
