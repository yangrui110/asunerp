
package com.hanlin.fadp.base.location;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * A special location resolver that uses Strings like URLs, but with more options
 *
 */

public class FadpHomeLocationResolver implements LocationResolver {

    public static final String envName = "fadp.home";

    public URL resolveLocation(String location) throws MalformedURLException {
        String propValue = System.getProperty(envName);
        if (propValue == null) {
            String errMsg = "The Java environment (-Dxxx=yyy) variable with name " + envName + " is not set, cannot resolve location.";
            throw new MalformedURLException(errMsg);
        }
        StringBuilder baseLocation = new StringBuilder(FlexibleLocation.stripLocationType(location));
        // if there is not a forward slash between the two, add it
        if (baseLocation.charAt(0) != '/' && propValue.charAt(propValue.length() - 1) != '/') {
            baseLocation.insert(0, '/');
        }
        baseLocation.insert(0, propValue);
        String fileLocation = baseLocation.toString();
        if (File.separatorChar != '/') {
            fileLocation = fileLocation.replace(File.separatorChar, '/');
        }
        if (!fileLocation.startsWith("/")) {
            fileLocation = "/".concat(fileLocation);
        }
        try {
            return new URI("file", null, fileLocation, null).toURL();
        } catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    }
}
