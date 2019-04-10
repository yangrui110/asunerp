
package com.hanlin.fadp.base.location;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A special location resolver that uses Strings like URLs, but with more options
 *
 */

public class StandardUrlLocationResolver implements LocationResolver {
    public URL resolveLocation(String location) throws MalformedURLException {
        return new URL(location);
    }
}
