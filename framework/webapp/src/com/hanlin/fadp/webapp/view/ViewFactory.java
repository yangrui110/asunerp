package com.hanlin.fadp.webapp.view;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralRuntimeException;
import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.webapp.control.ConfigXMLReader;

/**
 * @ClassName: ViewFactory 
 * @Description: 管理ViewHandler实例。通过获取controller.xml中handler的解析数据，采用工厂模式进行设计，
 * 这里没有使用常规的简单工厂、工厂方法或抽象工厂的设计思路，而是一种简单工厂的变体，
 * 具体来说，就是通过ConfigXMLReader获取controller.xml中有关handler的配置的map，
 * 该map中，key=handler的name属性值，为view的类型，如ftl，value=handler中class属性值，
 * 该属性值为ViewHandler接口的实现类，通过反射进行实例化，然后初始化，再放入到一个map中，
 * 该map的key=handler的name属性值，value=ViewHandler实现类的初始化后的实例
 * @author: 祖国
 * @date: 2015年12月17日 下午10:14:59
 */
public class ViewFactory {

    public static final String module = ViewFactory.class.getName();

    /**controller.xml中handler节点解析后构成的map，该节点的type属性值为view，map中key=name属性值，value=class属性值  */
    private final Map<String, ViewHandler> handlers = new HashMap<String, ViewHandler>();

    /**
     * @Title:ViewFactory
     * @Description:构建ViewHandler实现类的map，对handler节点的class属性值对应的类进行实例化和初始化，
     * 并设置key=default时，其value=com.hanlin.fadp.webapp.view.JspViewHandler的实例
     * @param context
     * @param controllerConfigURL
     */
    public ViewFactory(ServletContext context, URL controllerConfigURL) {
        // load all the view handlers
        try {
            Set<Map.Entry<String,String>> handlerEntries = ConfigXMLReader.getControllerConfig(controllerConfigURL).getViewHandlerMap().entrySet();
            if (handlerEntries != null) {
                for (Map.Entry<String,String> handlerEntry: handlerEntries) {
                    ViewHandler handler = (ViewHandler) ObjectType.getInstance(handlerEntry.getValue());
                    handler.setName(handlerEntry.getKey());
                    handler.init(context);
                    this.handlers.put(handlerEntry.getKey(), handler);
                }
            }
            // load the "default" type
            if (!this.handlers.containsKey("default")) {
                ViewHandler defaultHandler = (ViewHandler) ObjectType.getInstance("com.hanlin.fadp.webapp.view.JspViewHandler");
                defaultHandler.init(context);
                this. handlers.put("default", defaultHandler);
            }
        } catch (Exception e) {
            Debug.logError(e, module);
            throw new GeneralRuntimeException(e);
        }
    }

    public ViewHandler getViewHandler(String type) throws ViewHandlerException {
        if (UtilValidate.isEmpty(type)) {
            type = "default";
        }
        // get the view handler by type from the contextHandlers
        ViewHandler handler = handlers.get(type);
        if (handler == null) {
            throw new ViewHandlerException("No handler found for type: " + type);
        }
        return handler;
    }
}
