
package com.hanlin.fadp.service.jms;

import javax.jms.MessageListener;

import com.hanlin.fadp.service.GenericServiceException;

/**
 * GenericMessageListener - Estension to MessageListener
 */
public interface GenericMessageListener extends MessageListener {

    /**
     * Shutdown the listener and all connection(s).
     * @throws GenericServiceException
     */
    public void close() throws GenericServiceException;

    /**
     * Start the listener and all connection(s).
     * @throws GenericServiceException
     */
    public void load() throws GenericServiceException;

    /**
     * Refresh the connection.
     * @throws GenericServiceException
     */
    public void refresh() throws GenericServiceException;

    /**
     * Indicator if a connection is present.
     * @return true if connectio is present.
     */
    public boolean isConnected();

}
