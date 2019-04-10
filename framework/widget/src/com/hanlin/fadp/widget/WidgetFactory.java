
package com.hanlin.fadp.widget;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import com.hanlin.fadp.base.util.Assert;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.widget.model.IterateSectionWidget;
import com.hanlin.fadp.widget.model.ModelScreen;
import com.hanlin.fadp.widget.model.ModelScreenWidget;
import org.w3c.dom.Element;

/**
 * Screen widget factory.<p>Applications can add their own widget implementations
 * to the factory by implementing the {@link com.hanlin.fadp.widget.WidgetLoader} interface.</p>
 */
public class WidgetFactory {

    public static final String module = WidgetFactory.class.getName();
    protected static final Map<String, Constructor<? extends ModelScreenWidget>> screenWidgets = new ConcurrentHashMap<String, Constructor<? extends ModelScreenWidget>>();

    static {
        loadStandardWidgets();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Iterator<WidgetLoader> widgetLoaders = ServiceLoader.load(WidgetLoader.class, loader).iterator();
        while (widgetLoaders.hasNext()) {
            try {
                WidgetLoader widgetLoader = widgetLoaders.next();
                widgetLoader.loadWidgets();
            } catch (Exception e) {
                Debug.logError(e, module);
            }
        }
    }

    /**
     * Returns a <code>ModelScreenWidget</code> instance that implements the specified
     * XML element.
     * 
     * @param modelScreen The containing screen for the widget
     * @param element The widget XML element
     * @return a <code>ModelScreenWidget</code> instance that implements the specified
     * XML element
     * @throws IllegalArgumentException
     */
    public static ModelScreenWidget getModelScreenWidget(ModelScreen modelScreen, Element element) {
        Assert.notNull("modelScreen", modelScreen, "element", element);
        Constructor<? extends ModelScreenWidget> widgetConst = screenWidgets.get(element.getTagName());
        if (widgetConst == null) {
            throw new IllegalArgumentException("ModelScreenWidget class not found for element " + element.getTagName());
        }
        try {
            return widgetConst.newInstance(modelScreen, element);
        } catch (Exception e) {
            // log the original exception since the rethrown exception doesn't include much info about it and hides the cause
            Debug.logError(e, "Error getting widget for element " + element.getTagName(), module);
            throw new IllegalArgumentException(e.getMessage() + " for element " + element.getTagName());
        }
    }

    /**
     * Loads the standard OFBiz screen widgets.
     */
    protected static void loadStandardWidgets() {
        for (Class<?> clz: ModelScreenWidget.class.getClasses()) {
            try {
                // Subclass of ModelScreenWidget and non-abstract
                if (ModelScreenWidget.class.isAssignableFrom(clz) && (clz.getModifiers() & Modifier.ABSTRACT) == 0) {
                    try {
                        Field field = clz.getField("TAG_NAME");
                        Object fieldObject = field.get(null);
                        if (fieldObject != null) {
                            Class<? extends ModelScreenWidget> widgetClass = UtilGenerics.cast(clz);
                            registerScreenWidget(fieldObject.toString(), widgetClass);
                        }
                    } catch (Exception e) {}
                }
            } catch (Exception e) {
                Debug.logError(e, module);
            }
        }
        try {
            registerScreenWidget("iterate-section", IterateSectionWidget.class);
        } catch (Exception e) {
            Debug.logError(e, module);
        }
    }

    /**
     * Registers a screen sub-widget with the factory. If a tag name is already
     * registered, the new widget replaces the existing one.<p>The class supplied
     * to the method must have a public two-argument constructor that takes a
     * <code>ModelScreen</code> instance and an <code>Element</code> instance.</p>
     * 
     * @param tagName The XML element tag name for this widget
     * @param widgetClass The class that implements the widget element
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static void registerScreenWidget(String tagName, Class<? extends ModelScreenWidget> widgetClass) throws SecurityException, NoSuchMethodException {
        Assert.notNull("tagName", tagName, "widgetClass", widgetClass);
        screenWidgets.put(tagName, widgetClass.getConstructor(ModelScreen.class, Element.class));
        if (Debug.verboseOn()) {
            Debug.logVerbose("Registered " + widgetClass.getName() + " with tag name " + tagName, module);
        }
    }

    private WidgetFactory() {}
}
