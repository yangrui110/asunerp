
package com.hanlin.fadp.webapp.event;

import static com.hanlin.fadp.base.util.UtilGenerics.checkCollection;
import static com.hanlin.fadp.base.util.UtilGenerics.checkMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.GenericEntity;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.security.Security;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.LocalDispatcher;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.calendar.RecurrenceRule;
import com.hanlin.fadp.webapp.control.ConfigXMLReader.Event;
import com.hanlin.fadp.webapp.control.RequestHandler;

/**
 * CoreEvents - WebApp Events Related To Framework pieces
 */
public class CoreEvents {

    public static final String module = CoreEvents.class.getName();
    public static final String err_resource = "WebappUiLabels";

    /**
     * Return success event. Used as a place holder for events.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String returnSuccess(HttpServletRequest request, HttpServletResponse response) {
        return "success";
    }

    /**
     * Return error event. Used as a place holder for events.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String returnError(HttpServletRequest request, HttpServletResponse response) {
        return "error";
    }

    /**
     * Return null event. Used as a place holder for events.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String returnNull(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    /**
     * Schedule a service for a specific time or recurrence
     *  Request Parameters which are used for this service:
     *
     *  SERVICE_NAME      - Name of the service to invoke
     *  SERVICE_TIME      - First time the service will occur
     *  SERVICE_FREQUENCY - The type of recurrence (SECONDLY,MINUTELY,DAILY,etc)
     *  SERVICE_INTERVAL  - The interval of the frequency (every 5 minutes, etc)
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String scheduleService(HttpServletRequest request, HttpServletResponse response) {
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        //Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);

        Map<String, Object> params = UtilHttp.getParameterMap(request);
        // get the schedule parameters
        String jobName = (String) params.remove("JOB_NAME"); // 任务名称.
        String serviceName = (String) params.remove("SERVICE_NAME");	// 服务名称.
        String poolName = (String) params.remove("POOL_NAME");	// 线程池.
        String serviceTime = (String) params.remove("SERVICE_TIME"); // 起始时间.
        String serviceEndTime = (String) params.remove("SERVICE_END_TIME");	// 截止时间.
        String serviceFreq = (String) params.remove("SERVICE_FREQUENCY"); // 频率. 每分, 每秒, etc.
        String serviceIntr = (String) params.remove("SERVICE_INTERVAL"); // 不懂.
        String serviceCnt = (String) params.remove("SERVICE_COUNT"); // 任务执行的次数. -1表示没有限制
        String retryCnt = (String) params.remove("SERVICE_MAXRETRY"); // 最大重试次数. -1表示没有限制.

        // the frequency map
        Map<String, Integer> freqMap = new HashMap<String, Integer>();
        // 这个是定义的具体频率级别.
        freqMap.put("SECONDLY", Integer.valueOf(1));
        freqMap.put("MINUTELY", Integer.valueOf(2));
        freqMap.put("HOURLY", Integer.valueOf(3));
        freqMap.put("DAILY", Integer.valueOf(4));
        freqMap.put("WEEKLY", Integer.valueOf(5));
        freqMap.put("MONTHLY", Integer.valueOf(6));
        freqMap.put("YEARLY", Integer.valueOf(7));

        // some defaults
        long startTime = (new Date()).getTime(); // 默认配置. 当前时间.
        long endTime = 0; 
        int maxRetry = -1; // 出错后重试几次 ?
        int count = 1;	// 
        int interval = 1;
        int frequency = RecurrenceRule.DAILY; // 每天.频率.

        StringBuilder errorBuf = new StringBuilder();

        // make sure we passed a service
        if (serviceName == null) {
            String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.must_specify_service", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        // lookup the service definition to see if this service is externally available, if not require the SERVICE_INVOKE_ANY permission
        ModelService modelService = null;
        try {
            modelService = dispatcher.getDispatchContext().getModelService(serviceName);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Error looking up ModelService for serviceName [" + serviceName + "]", module);
            String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.error_modelservice_for_srv_name", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg + " [" + serviceName + "]: " + e.toString());
            return "error";
        }
        if (modelService == null) {
            String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.service_name_not_find", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg + " [" + serviceName + "]");
            return "error";
        }

        // make the context valid; using the makeValid method from ModelService
        Map<String, Object> serviceContext = new HashMap<String, Object>();
        Iterator<String> ci = modelService.getInParamNames().iterator();
        while (ci.hasNext()) {
            String name = ci.next();

            // don't include userLogin, that's taken care of below
            if ("userLogin".equals(name)) continue;
            // don't include locale, that is also taken care of below
            if ("locale".equals(name)) continue;

            Object value = request.getParameter(name);

            // if the parameter wasn't passed and no other value found, don't pass on the null
            if (value == null) {
                value = request.getAttribute(name);
            }
            if (value == null) {
                value = request.getSession().getAttribute(name);
            }
            if (value == null) {
                // still null, give up for this one
                continue;
            }

            if (value instanceof String && ((String) value).length() == 0) {
                // interpreting empty fields as null values for each in back end handling...
                value = null;
            }

            // set even if null so that values will get nulled in the db later on
            serviceContext.put(name, value);
        }
        serviceContext = modelService.makeValid(serviceContext, ModelService.IN_PARAM, true, null, timeZone, locale);

        if (userLogin != null) {
            serviceContext.put("userLogin", userLogin);
        }

        if (locale != null) {
            serviceContext.put("locale", locale);
        }

        if (!modelService.export && !security.hasPermission("SERVICE_INVOKE_ANY", request.getSession())) {
            String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.not_authorized_to_call", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        // some conversions 一些转换. 起始时间.
        if (UtilValidate.isNotEmpty(serviceTime)) {
            try { // serviceTime 转换.
                Timestamp ts1 = Timestamp.valueOf(serviceTime);
                startTime = ts1.getTime();
            } catch (IllegalArgumentException e) {
                try {
                    startTime = Long.parseLong(serviceTime);
                } catch (NumberFormatException nfe) {
                    String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.invalid_format_time", locale);
                    errorBuf.append(errMsg);
                }
            }
            // 如果当前时间小于 起始时间, 则会报错. SERVICE_TIME 已经传递.
            if (startTime < (new Date()).getTime()) {
                String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.service_time_already_passed", locale);
                errorBuf.append(errMsg);
            }
        }
        // 结束时间. 同开始时间解析一样.
        if (UtilValidate.isNotEmpty(serviceEndTime)) {
            try {
                Timestamp ts1 = Timestamp.valueOf(serviceEndTime);
                endTime = ts1.getTime();
            } catch (IllegalArgumentException e) {
                try {
                    endTime = Long.parseLong(serviceTime);
                } catch (NumberFormatException nfe) {
                    String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.invalid_format_time", locale);
                    errorBuf.append(errMsg);
                }
            }
            if (endTime < (new Date()).getTime()) {
                String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.service_time_already_passed", locale);
                errorBuf.append(errMsg);
            }
        }
        // 间隔. 不懂有何用.
        if (UtilValidate.isNotEmpty(serviceIntr)) {
            try {
                interval = Integer.parseInt(serviceIntr);
            } catch (NumberFormatException nfe) {
                String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.invalid_format_interval", locale);
                errorBuf.append(errMsg);
            }
        }
        // service 执行的次数.
        if (UtilValidate.isNotEmpty(serviceCnt)) {
            try {
                count = Integer.parseInt(serviceCnt);
            } catch (NumberFormatException nfe) {
                String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.invalid_format_count", locale);
                errorBuf.append(errMsg);
            }
        }
        // 执行的频率. 每分. 每小时. etc. 这里可以是合法的字符串.
        if (UtilValidate.isNotEmpty(serviceFreq)) {
            int parsedValue = 0;

            try {
                parsedValue = Integer.parseInt(serviceFreq);
                // parsedValue 有限制, 且合理.
                if (parsedValue > 0 && parsedValue < 8)
                    frequency = parsedValue;
            } catch (NumberFormatException nfe) {
                parsedValue = 0;
            }
            // 如果还是 0, 那么就一种情况, 字符串.
            if (parsedValue == 0) {
                if (!freqMap.containsKey(serviceFreq.toUpperCase())) {
                    String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.invalid_format_frequency", locale);
                    errorBuf.append(errMsg);
                } else {
                	// 合法的字符串.
                    frequency = freqMap.get(serviceFreq.toUpperCase()).intValue();
                }
            }
        }
        // 最大重试次数. 如果为 null, 给一个默认值. -1, 永远执行. 
        if (UtilValidate.isNotEmpty(retryCnt)) {
            int parsedValue = -2;

            try {
                parsedValue = Integer.parseInt(retryCnt);
            } catch (NumberFormatException e) {
                parsedValue = -2;
            }
            if (parsedValue > -2) {
                maxRetry = parsedValue;
            } else {
                maxRetry = modelService.maxRetry;
            }
        } else {
            maxRetry = modelService.maxRetry;
        }

        // return the errors
        if (errorBuf.length() > 0) {
            request.setAttribute("_ERROR_MESSAGE_", errorBuf.toString());
            return "error";
        }

        Map<String, Object> syncServiceResult = null;
        // schedule service
        try {
            if (null!=request.getParameter("_RUN_SYNC_") && request.getParameter("_RUN_SYNC_").equals("Y")) {
                syncServiceResult = dispatcher.runSync(serviceName, serviceContext);
            } else {
            	// 从这下面进入.
                dispatcher.schedule(jobName, poolName, serviceName, serviceContext, startTime, frequency, interval, count, endTime, maxRetry);
            }
        } catch (GenericServiceException e) {
            String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.service_dispatcher_exception", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg + e.getMessage());
            return "error";
        }

        String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.service_scheduled", locale);
        request.setAttribute("_EVENT_MESSAGE_", errMsg);
        if (null!=syncServiceResult) {
            request.getSession().setAttribute("_RUN_SYNC_RESULT_", syncServiceResult);
            return "sync_success";
        }
        return "success";
    }

    public static String saveServiceResultsToSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Locale locale = UtilHttp.getLocale(request);
        Map<String, Object> syncServiceResult = checkMap(session.getAttribute("_RUN_SYNC_RESULT_"), String.class, Object.class);
        if (null==syncServiceResult) {
            String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.no_fields_in_session", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        if (null!=request.getParameter("_CLEAR_PREVIOUS_PARAMS_") && request.getParameter("_CLEAR_PREVIOUS_PARAMS_").equalsIgnoreCase("on"))
            session.removeAttribute("_SAVED_SYNC_RESULT_");

        Map<String, String[]> serviceFieldsToSave = checkMap(request.getParameterMap(), String.class, String[].class);
        Map<String, Object> savedFields = new HashMap<String, Object>();

        for (Map.Entry<String, String[]> entry : serviceFieldsToSave.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() != null && "on".equalsIgnoreCase(request.getParameter(key)) && !"_CLEAR_PREVIOUS_PARAMS_".equals(key)) {
                String[] servicePath = key.split("\\|\\|");
                String partialKey = servicePath[servicePath.length-1];
                savedFields.put(partialKey, getObjectFromServicePath(key ,syncServiceResult));
            }
        }
        if (null!=session.getAttribute("_SAVED_SYNC_RESULT_")) {
            Map<String, Object> savedSyncResult = checkMap(session.getAttribute("_SAVED_SYNC_RESULT_"), String.class, Object.class);
            savedSyncResult.putAll(savedFields);
            savedFields = savedSyncResult;
        }
        session.setAttribute("_SAVED_SYNC_RESULT_", savedFields);
        return "success";
    }

    //Tries to return a map, if Object is one of Map, GenericEntity, List
    public static Object getObjectFromServicePath(String servicePath, Map<String, ? extends Object> serviceResult) {
        String[] sp = servicePath.split("\\|\\|");
        Object servicePathObject = null;
        Map<String, Object> servicePathMap = null;
        for (int i=0;i<sp.length;i++) {
            String servicePathEntry = sp[i];
            if (null==servicePathMap) {
                servicePathObject = serviceResult.get(servicePathEntry);
            } else {
                servicePathObject = servicePathMap.get(servicePathEntry);
            }
            servicePathMap = null;

            if (servicePathObject instanceof Map<?, ?>) {
                servicePathMap = checkMap(servicePathObject);
            } else if (servicePathObject instanceof GenericEntity) {
                GenericEntity servicePathEntity = (GenericEntity)servicePathObject;
                servicePathMap = new HashMap<String, Object>();
                for (Map.Entry<String, Object> entry: servicePathEntity.entrySet()) {
                    servicePathMap.put(entry.getKey(), entry.getValue());
                }
            } else if (servicePathObject instanceof Collection<?>) {
                Collection<?> servicePathColl = checkCollection(servicePathObject);
                int count=0;
                servicePathMap = new HashMap<String, Object>();
                for (Object value: servicePathColl) {
                    servicePathMap.put("_"+count+"_", value);
                    count++;
                }
            }
        }
        if (null==servicePathMap) {
            return servicePathObject;
        } else {
            return servicePathMap;
        }
    }

    public static ServiceEventHandler seh = new ServiceEventHandler();

    /**
     * Run a service.
     *  Request Parameters which are used for this event:
     *  SERVICE_NAME      - Name of the service to invoke
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String runService(HttpServletRequest request, HttpServletResponse response) {
        // get the mode and service name
        String serviceName = request.getParameter("serviceName");
        String mode = request.getParameter("mode");
        Locale locale = UtilHttp.getLocale(request);

        if (UtilValidate.isEmpty(serviceName)) {
            String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.must_specify_service_name", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        if (UtilValidate.isEmpty(mode)) {
            mode = "sync";
        }

        // now do a security check
        Security security = (Security) request.getAttribute("security");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        //lookup the service definition to see if this service is externally available, if not require the SERVICE_INVOKE_ANY permission
        ModelService modelService = null;
        try {
            modelService = dispatcher.getDispatchContext().getModelService(serviceName);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Error looking up ModelService for serviceName [" + serviceName + "]", module);
            String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.error_modelservice_for_srv_name", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg + "[" + serviceName + "]: " + e.toString());
            return "error";
        }
        if (modelService == null) {
            String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.service_name_not_find", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg + "[" + serviceName + "]");
            return "error";
        }

        if (!modelService.export && !security.hasPermission("SERVICE_INVOKE_ANY", request.getSession())) {
            String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.not_authorized_to_call", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg + ".");
            return "error";
        }

        Debug.logInfo("Running service named [" + serviceName + "] from event with mode [" + mode + "]", module);

        // call the service via the ServiceEventHandler which
        // adapts an event to a service.
        Event event = new Event("service", mode, serviceName, false);
        try {
            return seh.invoke(event, null, request, response);
        } catch (EventHandlerException e) {
            String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.service_eventhandler_exception", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg + ": " + e.getMessage());
            return "error";
        }
    }

    public static String streamFile(HttpServletRequest request, HttpServletResponse response) {
        //RequestHandler rh = (RequestHandler) request.getAttribute("_REQUEST_HANDLER_");
        String filePath = RequestHandler.getOverrideViewUri(request.getPathInfo());
        //String fileName = filePath.substring(filePath.lastIndexOf("/")+1);

        // load the file
        File file = new File(filePath);
        if (file.exists()) {
            Long longLen = Long.valueOf(file.length());
            int length = longLen.intValue();
            try {
                FileInputStream fis = new FileInputStream(file);
                UtilHttp.streamContentToBrowser(response, fis, length, null);
                fis.close();
            } catch (FileNotFoundException e) {
                Debug.logError(e, module);
                return "error";
            } catch (IOException e) {
                Debug.logError(e, module);
                return "error";
            }
        }
        return null;
    }
}
