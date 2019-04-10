
package com.hanlin.fadp.service.jms;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.JNDIContextFactory;
import com.hanlin.fadp.entity.Delegator;

/**
 * JmsTopicListener - Topic (Pub/Sub) Message Listener.
 */
public class JmsTopicListener extends AbstractJmsListener {

    public static final String module = JmsTopicListener.class.getName();

    private TopicConnection con = null;
    private TopicSession session = null;
    private Topic topic = null;

    private String jndiServer, jndiName, topicName, userName, password;

    /**
     * Creates a new JmsTopicListener - Should only be called by the JmsListenerFactory.
     */
    public JmsTopicListener(Delegator delegator, String jndiServer, String jndiName, String topicName, String userName, String password) {
        super(delegator);
        this.jndiServer = jndiServer;
        this.jndiName = jndiName;
        this.topicName = topicName;
        this.userName = userName;
        this.password = password;
    }

    public void close() throws GenericServiceException {
        try {
            if (session != null)
                session.close();
            if (con != null)
                con.close();
        } catch (JMSException e) {
            throw new GenericServiceException("Cannot close connection(s).", e);
        }
    }

    public synchronized void load() throws GenericServiceException {
        try {
            InitialContext jndi = JNDIContextFactory.getInitialContext(jndiServer);
            TopicConnectionFactory factory = (TopicConnectionFactory) jndi.lookup(jndiName);

            if (factory != null) {
                con = factory.createTopicConnection(userName, password);
                con.setExceptionListener(this);
                session = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                topic = (Topic) jndi.lookup(topicName);
                if (topic != null) {
                    TopicSubscriber subscriber = session.createSubscriber(topic);
                    subscriber.setMessageListener(this);
                    con.start();
                    this.setConnected(true);
                    if (Debug.infoOn()) Debug.logInfo("Listening to topic [" + topicName + "] on [" + jndiServer + "]...", module);
                } else {
                    throw new GenericServiceException("Topic lookup failed.");
                }
            } else {
                throw new GenericServiceException("Factory (broker) lookup failed.");
            }
        } catch (NamingException ne) {
            throw new GenericServiceException("JNDI lookup problems; listener not running.", ne);
        } catch (JMSException je) {
            throw new GenericServiceException("JMS internal error; listener not running.", je);
        } catch (GeneralException ge) {
            throw new GenericServiceException("Problems with InitialContext; listener not running.", ge);
        }
    }
}
