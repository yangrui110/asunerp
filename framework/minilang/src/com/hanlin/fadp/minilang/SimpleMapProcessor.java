
package com.hanlin.fadp.minilang;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.location.FlexibleLocation;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.minilang.operation.MapProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * SimpleMapProcessor Mini Language
 */
public class SimpleMapProcessor {

    private static final UtilCache<String, Map<String, MapProcessor>> simpleMapProcessorsResourceCache = UtilCache.createUtilCache("minilang.SimpleMapProcessorsResource", 0, 0);
    private static final UtilCache<URL, Map<String, MapProcessor>> simpleMapProcessorsURLCache = UtilCache.createUtilCache("minilang.SimpleMapProcessorsURL", 0, 0);

    protected static Map<String, MapProcessor> getAllProcessors(URL xmlURL) throws MiniLangException {
        Map<String, MapProcessor> mapProcessors = new HashMap<String, MapProcessor>();
        // read in the file
        Document document = null;
        try {
            document = UtilXml.readXmlDocument(xmlURL, true);
        } catch (java.io.IOException e) {
            throw new MiniLangException("Could not read XML file", e);
        } catch (org.xml.sax.SAXException e) {
            throw new MiniLangException("Could not parse XML file", e);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new MiniLangException("XML parser not setup correctly", e);
        }
        if (document == null) {
            throw new MiniLangException("Could not find SimpleMapProcessor XML document: " + xmlURL.toString());
        }
        Element rootElement = document.getDocumentElement();
        for (Element simpleMapProcessorElement : UtilXml.childElementList(rootElement, "simple-map-processor")) {
            MapProcessor processor = new MapProcessor(simpleMapProcessorElement);
            mapProcessors.put(simpleMapProcessorElement.getAttribute("name"), processor);
        }
        return mapProcessors;
    }

    protected static Map<String, MapProcessor> getProcessors(String xmlResource, String name, ClassLoader loader) throws MiniLangException {
        Map<String, MapProcessor> simpleMapProcessors = simpleMapProcessorsResourceCache.get(xmlResource);
        if (simpleMapProcessors == null) {
            URL xmlURL = null;
            try {
                xmlURL = FlexibleLocation.resolveLocation(xmlResource, loader);
            } catch (MalformedURLException e) {
                throw new MiniLangException("Could not find SimpleMapProcessor XML document in resource: " + xmlResource + "; error was: " + e.toString(), e);
            }
            if (xmlURL == null) {
                throw new MiniLangException("Could not find SimpleMapProcessor XML document in resource: " + xmlResource);
            }
            simpleMapProcessors = simpleMapProcessorsResourceCache.putIfAbsentAndGet(xmlResource, getAllProcessors(xmlURL));
        }
        return simpleMapProcessors;
    }

    protected static Map<String, MapProcessor> getProcessors(URL xmlURL, String name) throws MiniLangException {
        Map<String, MapProcessor> simpleMapProcessors = simpleMapProcessorsURLCache.get(xmlURL);
        if (simpleMapProcessors == null) {
            simpleMapProcessors = simpleMapProcessorsURLCache.putIfAbsentAndGet(xmlURL, getAllProcessors(xmlURL));
        }
        return simpleMapProcessors;
    }

    public static void runSimpleMapProcessor(String xmlResource, String name, Map<String, Object> inMap, Map<String, Object> results, List<Object> messages, Locale locale) throws MiniLangException {
        runSimpleMapProcessor(xmlResource, name, inMap, results, messages, locale, null);
    }

    public static void runSimpleMapProcessor(String xmlResource, String name, Map<String, Object> inMap, Map<String, Object> results, List<Object> messages, Locale locale, ClassLoader loader) throws MiniLangException {
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        Map<String, MapProcessor> mapProcessors = getProcessors(xmlResource, name, loader);
        MapProcessor processor = mapProcessors.get(name);
        if (processor == null) {
            throw new MiniLangException("Could not find SimpleMapProcessor named " + name + " in XML document resource: " + xmlResource);
        }
        if (processor != null) {
            processor.exec(inMap, results, messages, locale, loader);
        }
    }

    public static void runSimpleMapProcessor(URL xmlURL, String name, Map<String, Object> inMap, Map<String, Object> results, List<Object> messages, Locale locale, ClassLoader loader) throws MiniLangException {
        if (loader == null)
            loader = Thread.currentThread().getContextClassLoader();
        Map<String, MapProcessor> mapProcessors = getProcessors(xmlURL, name);
        MapProcessor processor = mapProcessors.get(name);
        if (processor == null) {
            throw new MiniLangException("Could not find SimpleMapProcessor named " + name + " in XML document: " + xmlURL.toString());
        }
        if (processor != null) {
            processor.exec(inMap, results, messages, locale, loader);
        }
    }
}
