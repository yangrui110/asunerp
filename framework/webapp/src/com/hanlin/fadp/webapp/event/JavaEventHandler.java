package com.hanlin.fadp.webapp.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.webapp.control.ConfigXMLReader;
import com.hanlin.fadp.webapp.control.ConfigXMLReader.Event;
import com.hanlin.fadp.webapp.control.ConfigXMLReader.RequestMap;

/**
 * @ClassName: JavaEventHandler 
 * @Description: 提供调用java类中静态方法的调用句柄，通过反射进行调用
 * @author: 祖国
 * @date: 2015年12月19日 下午3:50:46
 */
public class JavaEventHandler implements EventHandler {

    public static final String module = JavaEventHandler.class.getName();

    /** key=request-map节点中Event子节点的path属性值，为一个完整路径，value=为path属性值对应的class */
    private Map<String, Class<?>> eventClassMap = new HashMap<String, Class<?>>();

    /**
     * @see com.hanlin.fadp.webapp.event.EventHandler#init(javax.servlet.ServletContext)
     */
    public void init(ServletContext context) throws EventHandlerException {
    }

    /**
     * @see com.hanlin.fadp.webapp.event.EventHandler#invoke(ConfigXMLReader.Event, ConfigXMLReader.RequestMap, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public String invoke(Event event, RequestMap requestMap, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        //通过event节点的path属性值，为一个带包路径的类获取该类
    	Class<?> eventClass = this.eventClassMap.get(event.path);

        if (eventClass == null) {
            synchronized (this) {
                eventClass = this.eventClassMap.get(event.path);
                if (eventClass == null) {
                    try {
                        ClassLoader loader = Thread.currentThread().getContextClassLoader();
                        eventClass = loader.loadClass(event.path);//加载event节点的path属性值对应的类
                    } catch (ClassNotFoundException e) {
                        Debug.logError(e, "Error loading class with name: " + event.path + ", will not be able to run event...", module);
                    }
                    //将上面加载的类放入本类的map中
                    if (eventClass != null) {
                        eventClassMap.put(event.path, eventClass);
                    }
                }
            }
        }
        if (Debug.verboseOn()) Debug.logVerbose("[Set path/method]: " + event.path + " / " + event.invoke, module);

        Class<?>[] paramTypes = new Class<?>[] {HttpServletRequest.class, HttpServletResponse.class};

        Debug.logVerbose("*[[Event invocation]]*", module);
        Object[] params = new Object[] {request, response};

        //event.path值为带包名的类，event.invoke为具体调用的方法，eventClass为event.path对应的类
        return invoke(event.path, event.invoke, eventClass, paramTypes, params);
    }

    /**
     * @Title: invoke 
     * @Description: 反射调用类中的方法，类为eventClass，方法为eventMethod
     * @param eventPath request-map节点的event子节点的path属性值
     * @param eventMethod request-map节点的event子节点的invoke属性值
     * @param eventClass request-map节点的event子节点的path属性值对应的class
     * @param paramTypes 参数类型数组，{HttpServletRequest.class, HttpServletResponse.class}
     * @param params 调用本方法的参数数组，{request, response}
     * @throws EventHandlerException
     * @return: String 返回值，成功为success，失败则为error
     */
    private String invoke(String eventPath, String eventMethod, Class<?> eventClass, Class<?>[] paramTypes, Object[] params) throws EventHandlerException {
        if (eventClass == null) {
            throw new EventHandlerException("Error invoking event, the class " + eventPath + " was not found");
        }
        if (eventPath == null || eventMethod == null) {
            throw new EventHandlerException("Invalid event method or path; call initialize()");
        }

        Debug.logVerbose("[Processing]: JAVA Event", module);
        try {
        	//反射获取相应method
            Method m = eventClass.getMethod(eventMethod, paramTypes);
            //调用相应方法，由于是静态方法，所以第一个参数是null，如果不是静态方法，则第一个参数是方法所在对象
            String eventReturn = (String) m.invoke(null, params);

            if (Debug.verboseOn()) Debug.logVerbose("[Event Return]: " + eventReturn, module);
            return eventReturn;
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable t = e.getTargetException();

            if (t != null) {
                Debug.logError(t, "Problems Processing Event", module);
                throw new EventHandlerException("Problems processing event: " + t.toString(), t);
            } else {
                Debug.logError(e, "Problems Processing Event", module);
                throw new EventHandlerException("Problems processing event: " + e.toString(), e);
            }
        } catch (Exception e) {
            Debug.logError(e, "Problems Processing Event", module);
            throw new EventHandlerException("Problems processing event: " + e.toString(), e);
        }
    }
}
