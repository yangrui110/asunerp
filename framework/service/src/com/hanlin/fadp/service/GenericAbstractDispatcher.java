
package com.hanlin.fadp.service;

import java.util.Date;
import java.util.Map;

import javax.transaction.Transaction;

import com.hanlin.fadp.service.calendar.RecurrenceRule;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.transaction.GenericTransactionException;
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.security.Security;
import com.hanlin.fadp.service.jms.JmsListenerFactory;
import com.hanlin.fadp.service.job.JobManager;
import com.hanlin.fadp.service.job.JobManagerException;
import com.hanlin.fadp.base.util.Debug;

/**
 * Generic Services Local Dispatcher
 */
public abstract class GenericAbstractDispatcher implements LocalDispatcher {

    public static final String module = GenericAbstractDispatcher.class.getName();

    protected DispatchContext ctx = null;
    protected ServiceDispatcher dispatcher = null;
    protected String name = null;

    public GenericAbstractDispatcher() {}

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#schedule(java.lang.String, java.lang.String, java.util.Map, long, int, int, int, long, int)
     */
    public void schedule(String poolName, String serviceName, Map<String, ? extends Object> context, long startTime, int frequency, int interval, int count, long endTime, int maxRetry) throws GenericServiceException {
        schedule(null, poolName, serviceName, context, startTime, frequency, interval, count, endTime, maxRetry);
    }

    public void schedule(String poolName, String serviceName, long startTime, int frequency, int interval, int count, long endTime, int maxRetry, Object... context) throws GenericServiceException {
        schedule(poolName, serviceName, ServiceUtil.makeContext(context), startTime, frequency, interval, count, endTime, maxRetry);
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#schedule(java.lang.String, java.lang.String, java.lang.String, java.util.Map, long, int, int, int, long, int)
     */
    public void schedule(String jobName, String poolName, String serviceName, Map<String, ? extends Object> context, long startTime, int frequency, int interval, int count, long endTime, int maxRetry) throws GenericServiceException {
        Transaction suspendedTransaction = null;
        try {
            boolean beganTransaction = false;
            suspendedTransaction = TransactionUtil.suspend();
            try {
                beganTransaction = TransactionUtil.begin();
                try {
                    getJobManager().schedule(jobName, poolName, serviceName, context, startTime, frequency, interval, count, endTime, maxRetry);

                    if (Debug.verboseOn()) {
                        Debug.logVerbose("[LocalDispatcher.schedule] : Current time : " + (new Date()).getTime(), module);
                        Debug.logVerbose("[LocalDispatcher.schedule] : Runtime      : " + startTime, module);
                        Debug.logVerbose("[LocalDispatcher.schedule] : Frequency    : " + frequency, module);
                        Debug.logVerbose("[LocalDispatcher.schedule] : Interval     : " + interval, module);
                        Debug.logVerbose("[LocalDispatcher.schedule] : Count        : " + count, module);
                        Debug.logVerbose("[LocalDispatcher.schedule] : EndTime      : " + endTime, module);
                        Debug.logVerbose("[LocalDispatcher.schedule] : MazRetry     : " + maxRetry, module);
                    }

                } catch (JobManagerException jme) {
                    throw new GenericServiceException(jme.getMessage(), jme);
                }
            } catch (Exception e) {
                String errMsg = "General error while scheduling job";
                Debug.logError(e, errMsg, module);
                try {
                    TransactionUtil.rollback(beganTransaction, errMsg, e);
                } catch (GenericTransactionException gte1) {
                    Debug.logError(gte1, "Unable to rollback transaction", module);
                }
            } finally {
                try {
                    TransactionUtil.commit(beganTransaction);
                } catch (GenericTransactionException gte2) {
                    Debug.logError(gte2, "Unable to commit scheduled job", module);
                }
            }
        } catch (GenericTransactionException gte) {
            Debug.logError(gte, "Error suspending transaction while scheduling job", module);
        } finally {
            if (suspendedTransaction != null) {
                try {
                    TransactionUtil.resume(suspendedTransaction);
                } catch (GenericTransactionException gte3) {
                    Debug.logError(gte3, "Error resuming suspended transaction after scheduling job", module);
                }
            }
        }
    }

    public void schedule(String jobName, String poolName, String serviceName, long startTime, int frequency, int interval, int count, long endTime, int maxRetry, Object... context) throws GenericServiceException {
        schedule(jobName, poolName, serviceName, ServiceUtil.makeContext(context), startTime, frequency, interval, count, endTime, maxRetry);
    }

    public void addRollbackService(String serviceName, Map<String, ? extends Object> context, boolean persist) throws GenericServiceException {
        ServiceSynchronization.registerRollbackService(this.getDispatchContext(), serviceName, null, context, true, persist);
    }

    public void addRollbackService(String serviceName, boolean persist, Object... context) throws GenericServiceException {
        addRollbackService(serviceName, ServiceUtil.makeContext(context), persist);
    }

    public void addCommitService(String serviceName, Map<String, ? extends Object> context, boolean persist) throws GenericServiceException {
        ServiceSynchronization.registerCommitService(this.getDispatchContext(), serviceName, null, context, true, persist);
    }

    public void addCommitService(String serviceName, boolean persist, Object... context) throws GenericServiceException {
        addCommitService(serviceName, ServiceUtil.makeContext(context), persist);
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#schedule(java.lang.String, java.util.Map, long, int, int, int, long)
     */
    public void schedule(String serviceName, Map<String, ? extends Object> context, long startTime, int frequency, int interval, int count, long endTime) throws GenericServiceException {
        ModelService model = ctx.getModelService(serviceName);
        schedule(null, serviceName, context, startTime, frequency, interval, count, endTime, model.maxRetry);
    }

    public void schedule(String serviceName, long startTime, int frequency, int interval, int count, long endTime, Object... context) throws GenericServiceException {
        schedule(serviceName, ServiceUtil.makeContext(context), startTime, frequency, interval, count, endTime);
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#schedule(java.lang.String, java.util.Map, long, int, int, int)
     */
    public void schedule(String serviceName, Map<String, ? extends Object> context, long startTime, int frequency, int interval, int count) throws GenericServiceException {
        schedule(serviceName, context, startTime, frequency, interval, count, 0);
    }

    public void schedule(String serviceName, long startTime, int frequency, int interval, int count, Object... context) throws GenericServiceException {
        schedule(serviceName, ServiceUtil.makeContext(context), startTime, frequency, interval, count);
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#schedule(java.lang.String, java.util.Map, long, int, int, long)
     */
    public void schedule(String serviceName, Map<String, ? extends Object> context, long startTime, int frequency, int interval, long endTime) throws GenericServiceException {
        schedule(serviceName, context, startTime, frequency, interval, -1, endTime);
    }

    public void schedule(String serviceName, long startTime, int frequency, int interval, long endTime, Object... context) throws GenericServiceException {
        schedule(serviceName, ServiceUtil.makeContext(context), startTime, frequency, interval, endTime);
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#schedule(java.lang.String, java.util.Map, long)
     */
    public void schedule(String serviceName, Map<String, ? extends Object> context, long startTime) throws GenericServiceException {
        schedule(serviceName, context, startTime, RecurrenceRule.DAILY, 1, 1);
    }

    public void schedule(String serviceName, long startTime, Object... context) throws GenericServiceException {
        schedule(serviceName, ServiceUtil.makeContext(context), startTime);
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#getJobManager()
     */
    public JobManager getJobManager() {
        return dispatcher.getJobManager();
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#getJMSListeneFactory()
     */
    public JmsListenerFactory getJMSListeneFactory() {
        return dispatcher.getJMSListenerFactory();
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#getDelegator()
     */
    public Delegator getDelegator() {
        return dispatcher.getDelegator();
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#getSecurity()
     */
    public Security getSecurity() {
        return dispatcher.getSecurity();
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#getDispatchContext()
     */
    public DispatchContext getDispatchContext() {
        return ctx;
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#deregister()
     */
    public void deregister() {
        ServiceContainer.removeFromCache(getName());
        dispatcher.deregister(this);
    }

    /**
     * @see com.hanlin.fadp.service.LocalDispatcher#registerCallback(String, GenericServiceCallback)
     */
    public void registerCallback(String serviceName, GenericServiceCallback cb) {
        dispatcher.registerCallback(serviceName, cb);
    }
}

