
package com.hanlin.fadp.widget.renderer.fo;

import java.io.IOException;
import java.util.Map;

import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.widget.model.ModelScreenWidget;
import com.hanlin.fadp.widget.model.ModelScreenWidget.ColumnContainer;
import com.hanlin.fadp.widget.model.ModelWidget;
import com.hanlin.fadp.widget.renderer.ScreenStringRenderer;
import com.hanlin.fadp.widget.renderer.html.HtmlWidgetRenderer;

/**
 * Widget Library - HTML Form Renderer implementation
 * @deprecated Use MacroScreenRenderer.
 */
public class FoScreenRenderer extends HtmlWidgetRenderer implements ScreenStringRenderer {

    public static final String module = FoScreenRenderer.class.getName();

    public FoScreenRenderer() {}

    // This is a util method to get the style from a property file
    public static String getFoStyle(String styleName) {
        String value = UtilProperties.getPropertyValue("fo-styles.properties", styleName);
        if (value.equals(styleName)) {
            return "";
        }
        return value;
    }

    public String getRendererName() {
        return "xsl-fo";
    }

    public void renderScreenBegin(Appendable writer, Map<String, Object> context) throws IOException {
    }

    public void renderScreenEnd(Appendable writer, Map<String, Object> context) throws IOException {

    }

    public void renderSectionBegin(Appendable writer, Map<String, Object> context, ModelScreenWidget.Section section) throws IOException {
        if (section.isMainSection()) {
            this.widgetCommentsEnabled = ModelWidget.widgetBoundaryCommentsEnabled(context);
        }
        renderBeginningBoundaryComment(writer, section.isMainSection()?"Screen":"Section Widget", section);
    }
    public void renderSectionEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.Section section) throws IOException {
        renderEndingBoundaryComment(writer, section.isMainSection()?"Screen":"Section Widget", section);
    }

    public void renderContainerBegin(Appendable writer, Map<String, Object> context, ModelScreenWidget.Container container) throws IOException {
        writer.append("<fo:block");

        String style = container.getStyle(context);
        if (UtilValidate.isNotEmpty(style)) {
            writer.append(" ");
            writer.append(FoScreenRenderer.getFoStyle(style));
        }
        writer.append(">");
        appendWhitespace(writer);
    }
    public void renderContainerEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.Container container) throws IOException {
        writer.append("</fo:block>");
        appendWhitespace(writer);
    }

    public void renderLabel(Appendable writer, Map<String, Object> context, ModelScreenWidget.Label label) throws IOException {
        String labelText = label.getText(context);
        if (UtilValidate.isEmpty(labelText)) {
            // nothing to render
            return;
        }
        // open tag
        String style = label.getStyle(context);
        if (UtilValidate.isNotEmpty(style)) {
            writer.append("<fo:inline ");
            writer.append(FoScreenRenderer.getFoStyle(style));
            writer.append(">");
            // the text
            writer.append(labelText);
            // close tag
            writer.append("</fo:inline>");
        } else {
            writer.append(labelText);
        }
        appendWhitespace(writer);
    }

    public void renderHorizontalSeparator(Appendable writer, Map<String, Object> context, ModelScreenWidget.HorizontalSeparator separator) throws IOException {
        writer.append("<fo:block>");
        appendWhitespace(writer);
        writer.append("<fo:leader leader-length=\"100%\" leader-pattern=\"rule\" rule-style=\"solid\" rule-thickness=\"0.1mm\" color=\"black\"/>");
        appendWhitespace(writer);
        writer.append("</fo:block>");
        appendWhitespace(writer);
    }

    public void renderLink(Appendable writer, Map<String, Object> context, ModelScreenWidget.ScreenLink link) throws IOException {
        // TODO: not implemented
    }

    public void renderImage(Appendable writer, Map<String, Object> context, ModelScreenWidget.ScreenImage image) throws IOException {
        // TODO: not implemented
    }

    public void renderContentBegin(Appendable writer, Map<String, Object> context, ModelScreenWidget.Content content) throws IOException {
        // TODO: not implemented
    }

    public void renderContentBody(Appendable writer, Map<String, Object> context, ModelScreenWidget.Content content) throws IOException {
        // TODO: not implemented
    }

    public void renderContentEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.Content content) throws IOException {
        // TODO: not implemented
    }

    public void renderContentFrame(Appendable writer, Map<String, Object> context, ModelScreenWidget.Content content) throws IOException {
        // TODO: not implemented
    }

    public void renderSubContentBegin(Appendable writer, Map<String, Object> context, ModelScreenWidget.SubContent content) throws IOException {
        // TODO: not implemented
    }

    public void renderSubContentBody(Appendable writer, Map<String, Object> context, ModelScreenWidget.SubContent content) throws IOException {
        // TODO: not implemented
    }

    public void renderSubContentEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.SubContent content) throws IOException {
        // TODO: not implemented
    }

    public void renderScreenletBegin(Appendable writer, Map<String, Object> context, boolean collapsed, ModelScreenWidget.Screenlet screenlet) throws IOException {
        // TODO: not implemented
    }

    public void renderScreenletSubWidget(Appendable writer, Map<String, Object> context, ModelScreenWidget subWidget, ModelScreenWidget.Screenlet screenlet) throws GeneralException {
        // TODO: not implemented
    }

    public void renderScreenletEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.Screenlet screenlet) throws IOException {
        // TODO: not implemented
    }

    public void renderPortalPageBegin(Appendable writer, Map<String, Object> context, ModelScreenWidget.PortalPage portalPage) throws GeneralException, IOException {
        // TODO: not implemented
    }
    public void renderPortalPageEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.PortalPage portalPage) throws GeneralException, IOException {
        // TODO: not implemented
    }
    public void renderPortalPageColumnBegin(Appendable writer, Map<String, Object> context, ModelScreenWidget.PortalPage portalPage, GenericValue portalPageColumn) throws GeneralException, IOException {
        // TODO: not implemented
    }
    public void renderPortalPageColumnEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.PortalPage portalPage, GenericValue portalPageColumn) throws GeneralException, IOException {
        // TODO: not implemented
    }
    public void renderPortalPagePortletBegin(Appendable writer, Map<String, Object> context, ModelScreenWidget.PortalPage portalPage, GenericValue portalPortlet) throws GeneralException, IOException {
        // TODO: not implemented
    }
    public void renderPortalPagePortletEnd(Appendable writer, Map<String, Object> context, ModelScreenWidget.PortalPage portalPage, GenericValue portalPortlet) throws GeneralException, IOException {
        // TODO: not implemented
    }
    public void renderPortalPagePortletBody(Appendable writer, Map<String, Object> context, ModelScreenWidget.PortalPage portalPage, GenericValue portalPortlet) throws GeneralException, IOException {
        // TODO: not implemented
    }

    @Override
    public void renderColumnContainer(Appendable writer, Map<String, Object> context, ColumnContainer columnContainer) throws IOException {
        // TODO: not implemented
    }
}
