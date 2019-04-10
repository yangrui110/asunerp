
package com.hanlin.fadp.webapp.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import com.hanlin.fadp.base.util.Base64;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/** Utility methods needed to implement a WebDAV servlet. */
public class WebDavUtil {

    public static final String module = WebDavUtil.class.getName();
    public static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
    public static final String RFC1123_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    public static String formatDate(String formatString, Date date) {
        DateFormat df = new SimpleDateFormat(formatString);
        df.setTimeZone(GMT_TIMEZONE);
        return df.format(date);
    }

    public static Document getDocumentFromRequest(HttpServletRequest request) throws IOException, SAXException, ParserConfigurationException {
        Document document = null;
        InputStream is = null;
        try {
            is = request.getInputStream();
            document = UtilXml.readXmlDocument(is, false, "WebDAV request");
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return document;
    }

    /** Returns a <code>Map</code> containing user credentials found in the request. Returns
     * <code>null</code> if no user credentials were found. The returned <code>Map</code> is
     * intended to be used as parameters for the <code>userLogin</code> service. <p>The method
     * checks for the request parameters <code>USERNAME</code> and <code>PASSWORD</code>. If
     * those aren't found, then the request is checked for the HTTP Authorization header.
     * Currently, only Basic authorization is supported.</p>
     *
     * @param request The WebDAV request
     * @return A <code>Map</code> containing <code>login.username</code> and
     * <code>login.password</code> elements.
     */
    public static Map<String, Object> getCredentialsFromRequest(HttpServletRequest request) {
        String username = request.getParameter("USERNAME");
        String password = request.getParameter("PASSWORD");
        if (UtilValidate.isEmpty(username) || UtilValidate.isEmpty(password)) {
            String credentials = request.getHeader("Authorization");
            if (credentials != null && credentials.startsWith("Basic ")) {
                credentials = Base64.base64Decode(credentials.replace("Basic ", ""));
                Debug.logVerbose("Found HTTP Basic credentials", module);
                String[] parts = credentials.split(":");
                if (parts.length < 2) {
                    return null;
                }
                username = parts[0];
                password = parts[1];
            } else {
                return null;
            }
        }
        if ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "username.lowercase"))) {
            username = username.toLowerCase();
        }
        if ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "password.lowercase"))) {
            password = password.toLowerCase();
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("login.username", username);
        result.put("login.password", password);
        return result;
    }
}
