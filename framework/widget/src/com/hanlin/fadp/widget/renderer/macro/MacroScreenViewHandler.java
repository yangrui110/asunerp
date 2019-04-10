
package com.hanlin.fadp.widget.renderer.macro;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.UtilCodec;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.collections.MapStack;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.util.EntityUtilProperties;
import com.hanlin.fadp.service.LocalDispatcher;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceUtil;
import com.hanlin.fadp.webapp.view.AbstractViewHandler;
import com.hanlin.fadp.webapp.view.ViewHandlerException;
import com.hanlin.fadp.widget.renderer.FormStringRenderer;
import com.hanlin.fadp.widget.renderer.MenuStringRenderer;
import com.hanlin.fadp.widget.renderer.ScreenRenderer;
import com.hanlin.fadp.widget.renderer.ScreenStringRenderer;
import com.hanlin.fadp.widget.renderer.TreeStringRenderer;
import org.xml.sax.SAXException;

import freemarker.template.TemplateException;
import freemarker.template.utility.StandardCompress;

public class MacroScreenViewHandler extends AbstractViewHandler {

    public static final String module = MacroScreenViewHandler.class.getName();

    protected ServletContext servletContext = null;

    public void init(ServletContext context) throws ViewHandlerException {
        this.servletContext = context;
    }

    private ScreenStringRenderer loadRenderers(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> context, Writer writer) throws GeneralException, TemplateException, IOException {
        String screenMacroLibraryPath = UtilProperties.getPropertyValue("widget", getName() + ".screenrenderer");
        String formMacroLibraryPath = UtilProperties.getPropertyValue("widget", getName() + ".formrenderer");
        String treeMacroLibraryPath = UtilProperties.getPropertyValue("widget", getName() + ".treerenderer");
        String menuMacroLibraryPath = UtilProperties.getPropertyValue("widget", getName() + ".menurenderer");
        Map<String, Object> userPreferences = UtilGenerics.cast(context.get("userPreferences"));
        if (userPreferences != null) {
            String visualThemeId = (String) userPreferences.get("VISUAL_THEME");
            if (visualThemeId != null) {
                LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
                Map<String, Object> serviceCtx = dispatcher.getDispatchContext().makeValidContext("getVisualThemeResources",
                        ModelService.IN_PARAM, context);
                serviceCtx.put("visualThemeId", visualThemeId);
                Map<String, Object> serviceResult = dispatcher.runSync("getVisualThemeResources", serviceCtx);
                if (ServiceUtil.isSuccess(serviceResult)) {
                    Map<String, List<String>> themeResources = UtilGenerics.cast(serviceResult.get("themeResources"));
                    List<String> resourceList = UtilGenerics.cast(themeResources.get("VT_SCRN_MACRO_LIB"));
                    if (resourceList != null && !resourceList.isEmpty()) {
                        String macroLibraryPath = resourceList.get(0);
                        if (macroLibraryPath != null) {
                            screenMacroLibraryPath = macroLibraryPath;
                        }
                    }
                    resourceList = UtilGenerics.cast(themeResources.get("VT_FORM_MACRO_LIB"));
                    if (resourceList != null && !resourceList.isEmpty()) {
                        String macroLibraryPath = resourceList.get(0);
                        if (macroLibraryPath != null) {
                            formMacroLibraryPath = macroLibraryPath;
                        }
                    }
                    resourceList = UtilGenerics.cast(themeResources.get("VT_TREE_MACRO_LIB"));
                    if (resourceList != null && !resourceList.isEmpty()) {
                        String macroLibraryPath = resourceList.get(0);
                        if (macroLibraryPath != null) {
                            treeMacroLibraryPath = macroLibraryPath;
                        }
                    }
                    resourceList = UtilGenerics.cast(themeResources.get("VT_MENU_MACRO_LIB"));
                    if (resourceList != null && !resourceList.isEmpty()) {
                        String macroLibraryPath = resourceList.get(0);
                        if (macroLibraryPath != null) {
                            menuMacroLibraryPath = macroLibraryPath;
                        }
                    }
                }
            }
        }
        ScreenStringRenderer screenStringRenderer = new MacroScreenRenderer(UtilProperties.getPropertyValue("widget", getName()
                + ".name"), screenMacroLibraryPath);
        if (!formMacroLibraryPath.isEmpty()) {
            FormStringRenderer formStringRenderer = new MacroFormRenderer(formMacroLibraryPath, request, response);
            context.put("formStringRenderer", formStringRenderer);
        }
        if (!treeMacroLibraryPath.isEmpty()) {
            TreeStringRenderer treeStringRenderer = new MacroTreeRenderer(treeMacroLibraryPath, writer);
            context.put("treeStringRenderer", treeStringRenderer);
        }
        if (!menuMacroLibraryPath.isEmpty()) {
            MenuStringRenderer menuStringRenderer = new MacroMenuRenderer(menuMacroLibraryPath, request, response);
            context.put("menuStringRenderer", menuStringRenderer);
        }
        return screenStringRenderer;
    }

    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        try {
            Writer writer = response.getWriter();
            Delegator delegator = (Delegator) request.getAttribute("delegator");
            // compress output if configured to do so
            if (UtilValidate.isEmpty(encoding)) {
                encoding = EntityUtilProperties.getPropertyValue("widget", getName() + ".default.encoding", "none", delegator);
            }
            boolean compressOutput = "compressed".equals(encoding);
            if (!compressOutput) {
                compressOutput = "true".equals(EntityUtilProperties.getPropertyValue("widget", getName() + ".compress", delegator));
            }
            if (!compressOutput && this.servletContext != null) {
                compressOutput = "true".equals(this.servletContext.getAttribute("compressHTML"));
            }
            if (compressOutput) {
                // StandardCompress defaults to a 2k buffer. That could be increased
                // to speed up output.
                writer = new StandardCompress().getWriter(writer, null);
            }
            MapStack<String> context = MapStack.create();
            ScreenRenderer.populateContextForRequest(context, null, request, response, servletContext);
            ScreenStringRenderer screenStringRenderer = loadRenderers(request, response, context, writer);
            ScreenRenderer screens = new ScreenRenderer(writer, context, screenStringRenderer);
            context.put("screens", screens);
            context.put("simpleEncoder", UtilCodec.getEncoder(UtilProperties.getPropertyValue("widget", getName() + ".encoder")));
            screenStringRenderer.renderScreenBegin(writer, context);
            screens.render(page);
            screenStringRenderer.renderScreenEnd(writer, context);
            writer.flush();
        } catch (TemplateException e) {
            Debug.logError(e, "Error initializing screen renderer", module);
            throw new ViewHandlerException(e.getMessage());
        } catch (IOException e) {
            throw new ViewHandlerException("Error in the response writer/output stream: " + e.toString(), e);
        } catch (SAXException e) {
            throw new ViewHandlerException("XML Error rendering page: " + e.toString(), e);
        } catch (ParserConfigurationException e) {
            throw new ViewHandlerException("XML Error rendering page: " + e.toString(), e);
        } catch (GeneralException e) {
            throw new ViewHandlerException("Lower level error rendering page: " + e.toString(), e);
        }
    }
}
