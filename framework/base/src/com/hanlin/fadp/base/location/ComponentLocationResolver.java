
package com.hanlin.fadp.base.location;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.hanlin.fadp.base.component.ComponentConfig;
import com.hanlin.fadp.base.component.ComponentException;
import com.hanlin.fadp.base.util.Debug;

/**
 * A special location resolver that uses Strings like URLs, but with more options
 *
 */

public class ComponentLocationResolver implements LocationResolver {

    public static final String module = ComponentLocationResolver.class.getName();

    public URL resolveLocation(String location) throws MalformedURLException {
        String baseLocation = getBaseLocation(location).toString();
        if (File.separatorChar != '/') {
            baseLocation = baseLocation.replace(File.separatorChar, '/');
        }
        if (!baseLocation.startsWith("/")) {
            baseLocation = "/".concat(baseLocation);
        }
        try {
            return new URI("file", null, baseLocation, null).toURL();
        } catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    }

    public static StringBuilder getBaseLocation(String location) throws MalformedURLException {
        StringBuilder baseLocation = new StringBuilder(FlexibleLocation.stripLocationType(location));
        // componentName is between the first slash and the second
        int firstSlash = baseLocation.indexOf("/");
        int secondSlash = baseLocation.indexOf("/", firstSlash + 1);
        if (firstSlash != 0 || secondSlash == -1) {
            throw new MalformedURLException("Bad component location [" + location + "]: base location missing slashes [" + baseLocation + "], first = " + firstSlash + ", second = " + secondSlash + "; should be like: component://{component-name}/relative/path");
        }
        String componentName = baseLocation.substring(firstSlash + 1, secondSlash);
        // got the componentName, now remove it from the baseLocation, removing the second slash too (just in case the rootLocation has one)
        baseLocation.delete(0, secondSlash + 1);
        try {
            String rootLocation = ComponentConfig.getRootLocation(componentName);
            // if there is not a forward slash between the two, add it
            if (baseLocation.charAt(0) != '/' && rootLocation.charAt(rootLocation.length() - 1) != '/') {
                baseLocation.insert(0, '/');
            }
            // insert the root location and we're done
            baseLocation.insert(0, rootLocation);
            return baseLocation;
        } catch (ComponentException e) {
            String errMsg = "Could not get root location for component with name [" + componentName + "], error was: " + e.toString();
            Debug.logError(e, errMsg, module);
            throw new MalformedURLException(errMsg);
        }
    }
}
