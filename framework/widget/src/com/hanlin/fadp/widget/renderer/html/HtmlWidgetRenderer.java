
package com.hanlin.fadp.widget.renderer.html;

import java.io.IOException;

import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.widget.model.ModelWidget;

/**
 * Widget Library - HTML Widget Renderer implementation. HtmlWidgetRenderer
 * is a base class that is extended by other widget HTML rendering classes.
 */
public class HtmlWidgetRenderer {
    public static final String module = HtmlWidgetRenderer.class.getName();

    /**
     * Characters that are appended to the end of each rendered element. Currently set to
     * CR/LF.
     */
    public static final String whiteSpace = "\r\n";

    protected boolean widgetCommentsEnabled = false;

    /**
     * Helper method used to append whitespace characters to the end of each rendered element.
     * @param writer The writer to write to
     */
    public void appendWhitespace(Appendable writer) throws IOException {
        writer.append(whiteSpace);
    }

    /**
     * Helper method used to build the boundary comment string.
     * @param boundaryType The boundary type: "Begin" or "End"
     * @param widgetType The widget type: "Screen Widget", "Form Widget", etc.
     * @param widgetName The widget name
     */
    public String buildBoundaryComment(String boundaryType, String widgetType, String widgetName) {
        return formatBoundaryComment(boundaryType, widgetType, widgetName);
    }

    public static String formatBoundaryComment(String boundaryType, String widgetType, String widgetName) {
        return "<!-- " + boundaryType + " " + widgetType + " " + widgetName + " -->" + whiteSpace;
    }

    /**
     * Renders the beginning boundary comment string.
     * @param writer The writer to write to
     * @param widgetType The widget type: "Screen Widget", "Form Widget", etc.
     * @param modelWidget The widget
     */
    public void renderBeginningBoundaryComment(Appendable writer, String widgetType, ModelWidget modelWidget) throws IOException {
        if (this.widgetCommentsEnabled) {
            writer.append(this.buildBoundaryComment("Begin", widgetType, modelWidget.getBoundaryCommentName()));
        }
    }

    /**
     * Renders the ending boundary comment string.
     * @param writer The writer to write to
     * @param widgetType The widget type: "Screen Widget", "Form Widget", etc.
     * @param modelWidget The widget
     */
    public void renderEndingBoundaryComment(Appendable writer, String widgetType, ModelWidget modelWidget) throws IOException {
        if (this.widgetCommentsEnabled) {
            writer.append(this.buildBoundaryComment("End", widgetType, modelWidget.getBoundaryCommentName()));
        }
    }

    /** Extracts parameters from a target URL string, prepares them for an Ajax
     * JavaScript call. This method is currently set to return a parameter string
     * suitable for the Prototype.js library.
     * @param target Target URL string
     * @return Parameter string
     */
    public static String getAjaxParamsFromTarget(String target) {
        String targetParams = UtilHttp.getQueryStringFromTarget(target);
        targetParams = targetParams.replace("?", "");
        targetParams = targetParams.replace("&amp;", "&");
        return targetParams;
    }
}
