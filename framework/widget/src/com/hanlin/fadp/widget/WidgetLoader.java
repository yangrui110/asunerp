
package com.hanlin.fadp.widget;

/**
 *  A service that registers screen widget classes with the screen widget factory.
 *  Applications implement this interface to add their widget implementations
 *  to the OFBiz framework.<p>Implementations must have their class names
 *  in the <code>META-INF/service/com.hanlin.fadp.widget.WidgetLoader</code> file.</p> 
 */
public interface WidgetLoader {

    /**
     * Registers screen widgets with the widget factory.<p>Implementations register
     * screen widget classes by calling the <code>WidgetFactory registerXxxx</code>
     * methods.</p>
     */
    void loadWidgets();

}
