package com.hanlin.fadp.webapp.event;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralRuntimeException;
import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.webapp.control.ConfigXMLReader;

/**
 * @ClassName: EventFactory 
 * @Description: 管理EventHandler实例数据。通过加载controller.xml中handler解析数据，其中handler类型为非view类型，
 * 如果类型为view类型，则调用ViewFactory，因此，本类的设计和ViewFactory设计是一样的
 * @author: 祖国
 * @date: 2015年12月18日 上午10:05:21
 */
public class EventFactory {

    public static final String module = EventFactory.class.getName();

    /**event处理句柄map, key=handler中name属性值，value=class属性值对于的实例，该实例已经初始化*/
    private final Map<String, EventHandler> handlers = new HashMap<String, EventHandler>();

    /**
     * @Title:EventFactory
     * @Description:EventFactory构造方法， 通过controller.xml中handler节点的class属性值，
     * （为EventHandler实现类），构建相应实例，并初始化
     * @param context 请求上下文
     * @param controllerConfigURL controller.xml对应的url
     */
    public EventFactory(ServletContext context, URL controllerConfigURL) {
        // load all the event handlers
        try {
            Set<Map.Entry<String,String>> handlerEntries = ConfigXMLReader.getControllerConfig(controllerConfigURL).getEventHandlerMap().entrySet();
            if (handlerEntries != null) {
                for (Map.Entry<String,String> handlerEntry: handlerEntries) {
                    EventHandler handler = (EventHandler) ObjectType.getInstance(handlerEntry.getValue());
                    handler.init(context);
                    this.handlers.put(handlerEntry.getKey(), handler);
                }
            }
        } catch (Exception e) {
            Debug.logError(e, module);
            throw new GeneralRuntimeException(e);
        }
    }

    /**
     * @Title: getEventHandler 
     * @Description: 获取eventhandler实例
     * @param type 出入的字符串，
     * @throws EventHandlerException
     * @return: EventHandler
     */
    public EventHandler getEventHandler(String type) throws EventHandlerException {
        EventHandler handler = handlers.get(type);
        if (handler == null) {
            throw new EventHandlerException("No handler found for type: " + type);
        }
        return handler;
    }
}
