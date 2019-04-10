
package com.hanlin.fadp.service.jms;

import java.util.Map;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.serialize.XmlSerializer;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.LocalDispatcher;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceContainer;

/**
 * AbstractJmsListener
 */
public abstract class AbstractJmsListener implements GenericMessageListener, ExceptionListener {

    public static final String module = AbstractJmsListener.class.getName();

    protected LocalDispatcher dispatcher;
    protected boolean isConnected = false;

    /**
     * Initializes the LocalDispatcher for this service listener.
     * @param delegator the delegator associated to the dispatcher
     */
    protected AbstractJmsListener(Delegator delegator) {
        this.dispatcher = ServiceContainer.getLocalDispatcher("JMSDispatcher", delegator);
    }

    /**
     * Runs the service defined in the MapMessage
     * @param message
     * @return Map
     */
    protected Map<String, Object> runService(MapMessage message) {
        Map<String, ? extends Object> context = null;
        String serviceName = null;
        String xmlContext = null;

        try {
            serviceName = message.getString("serviceName");
            xmlContext = message.getString("serviceContext");
            if (serviceName == null || xmlContext == null) {
                Debug.logError("Message received is not an OFB service message. Ignored!", module);
                return null;
            }
            /** OFBiz JMS 在传输 Message 的时候，将 service 的 context 序列化了 */
            Object o = XmlSerializer.deserialize(xmlContext, dispatcher.getDelegator());

            if (Debug.verboseOn()) Debug.logVerbose("De-Serialized Context --> " + o, module);
            if (ObjectType.instanceOf(o, "java.util.Map"))
                context = UtilGenerics.checkMap(o);
        } catch (JMSException je) {
            Debug.logError(je, "Problems reading message.", module);
        } catch (Exception e) {
            Debug.logError(e, "Problems deserializing the service context.", module);
        }

        try {
            ModelService model = dispatcher.getDispatchContext().getModelService(serviceName);
            if (!model.export) {
                Debug.logWarning("Attempt to invoke a non-exported service: " + serviceName, module);
                return null;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, "Unable to get ModelService for service : " + serviceName, module);
        }

        if (Debug.verboseOn()) Debug.logVerbose("Running service: " + serviceName, module);

        Map<String, Object> result = null;
        if (context != null) {
            try {
                result = dispatcher.runSync(serviceName, context);
            } catch (GenericServiceException gse) {
                Debug.logError(gse, "Problems with service invocation.", module);
            }
        }
        return result;
    }

    /**
     * Receives the MapMessage and processes the service.
     * @see javax.jms.MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
        MapMessage mapMessage = null;

        if (Debug.verboseOn()) Debug.logVerbose("JMS Message Received --> " + message, module);
        /** 只处理 MapMessage */
        if (message instanceof MapMessage) {
            mapMessage = (MapMessage) message;
        } else {
            Debug.logError("Received message is not a MapMessage!", module);
            return;
        }
        runService(mapMessage);
    }

    /**
     * On exception try to re-establish connection to the JMS server.
     * @see javax.jms.ExceptionListener#onException(JMSException)
     */
    public void onException(JMSException je) {
        this.setConnected(false);
        Debug.logError(je, "JMS connection exception", module);
        while (!isConnected()) {
            try {
                this.refresh();
            } catch (GenericServiceException e) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {}
                continue;
            }
        }
    }

    /**
     *
     * @see com.hanlin.fadp.service.jms.GenericMessageListener#refresh()
     */
    public void refresh() throws GenericServiceException {
        this.close();
        this.load();
    }

    /**
     *
     * @see com.hanlin.fadp.service.jms.GenericMessageListener#isConnected()
     */
    public boolean isConnected() {
        return this.isConnected;
    }

    /**
     * Setter method for the connected field.
     * @param connected
     */
    protected void setConnected(boolean connected) {
        this.isConnected = connected;
    }

}
