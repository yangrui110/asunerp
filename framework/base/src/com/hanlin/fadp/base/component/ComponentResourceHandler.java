
package com.hanlin.fadp.base.component;

import java.io.InputStream;
import java.net.URL;

import com.hanlin.fadp.base.config.GenericConfigException;
import com.hanlin.fadp.base.config.ResourceHandler;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.Debug;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Contains resource information and provides for loading data
 *
 */
@SuppressWarnings("serial")
public class ComponentResourceHandler implements ResourceHandler {

    public static final String module = ComponentResourceHandler.class.getName();
    protected String componentName;
    protected String loaderName;
    protected String location;

    public ComponentResourceHandler(String componentName, Element element) {
        this.componentName = componentName;
        this.loaderName = element.getAttribute("loader");
        this.location = element.getAttribute("location");
    }

    public ComponentResourceHandler(String componentName, String loaderName, String location) {
        this.componentName = componentName;
        this.loaderName = loaderName;
        this.location = location;
        if (Debug.verboseOn()) Debug.logVerbose("Created " + this.toString(), module);
    }

    public String getLoaderName() {
        return this.loaderName;
    }

    public String getLocation() {
        return this.location;
    }

    public Document getDocument() throws GenericConfigException {
        try {
            return UtilXml.readXmlDocument(this.getStream(), this.getFullLocation(), true);
        } catch (org.xml.sax.SAXException e) {
            throw new GenericConfigException("Error reading " + this.toString(), e);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new GenericConfigException("Error reading " + this.toString(), e);
        } catch (java.io.IOException e) {
            throw new GenericConfigException("Error reading " + this.toString(), e);
        }
    }

    public InputStream getStream() throws GenericConfigException {
        return ComponentConfig.getStream(componentName, loaderName, location);
    }

    public URL getURL() throws GenericConfigException {
        return ComponentConfig.getURL(componentName, loaderName, location);
    }

    public boolean isFileResource() throws GenericConfigException {
        return ComponentConfig.isFileResourceLoader(componentName, loaderName);
    }

    public String getFullLocation() throws GenericConfigException {
        return ComponentConfig.getFullLocation(componentName, loaderName, location);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComponentResourceHandler) {
            ComponentResourceHandler other = (ComponentResourceHandler) obj;

            if (this.loaderName.equals(other.loaderName) &&
                this.componentName.equals(other.componentName) &&
                this.location.equals(other.location)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        // the hashCode will weight by a combination componentName and the combination of loaderName and location
        return (this.componentName.hashCode() + ((this.loaderName.hashCode() + this.location.hashCode()) >> 1)) >> 1;
    }

    @Override
    public String toString() {
        return "ComponentResourceHandler from XML file [" + this.componentName + "] with loaderName [" + loaderName + "] and location [" + location + "]";
    }
}
