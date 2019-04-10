
package com.hanlin.fadp.service.job;

import java.util.Date;

import com.hanlin.fadp.base.util.Assert;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.transaction.GenericTransactionException;
import com.hanlin.fadp.entity.transaction.TransactionUtil;

/**
 * Abstract Job.
 */
public abstract class AbstractJob implements Job {

    public static final String module = AbstractJob.class.getName();

    private final String jobId;
    private final String jobName;
    protected State currentState = State.CREATED;
    private long elapsedTime = 0;
    private final Date startTime = new Date();

    protected AbstractJob(String jobId, String jobName) {
        Assert.notNull("jobId", jobId, "jobName", jobName);
        this.jobId = jobId;
        this.jobName = jobName;
    }

    @Override
    public State currentState() {
        return currentState;
    }

    @Override
    public String getJobId() {
        return this.jobId;
    }

    @Override
    public String getJobName() {
        return this.jobName;
    }

    @Override
    public void queue() throws InvalidJobException {
        if (currentState != State.CREATED) {
            throw new InvalidJobException("Illegal state change");
        }
        this.currentState = State.QUEUED;
    }

    @Override
    public void deQueue() throws InvalidJobException {
        if (currentState != State.QUEUED) {
            throw new InvalidJobException("Illegal state change");
        }
        this.currentState = State.CREATED;
    }

    /**
     *  Executes this Job. The {@link #run()} method calls this method.
     */
    public abstract void exec() throws InvalidJobException;

    @Override
    public void run() {
        long startMillis = System.currentTimeMillis();
        try {
            exec();
        } catch (InvalidJobException e) {
            Debug.logWarning(e.getMessage(), module);
        }
        // sanity check; make sure we don't have any transactions in place
        try {
            // roll back current TX first
            if (TransactionUtil.isTransactionInPlace()) {
                Debug.logWarning("*** NOTICE: JobInvoker finished w/ a transaction in place! Rolling back.", module);
                TransactionUtil.rollback();
            }
            // now resume/rollback any suspended txs
            if (TransactionUtil.suspendedTransactionsHeld()) {
                int suspended = TransactionUtil.cleanSuspendedTransactions();
                Debug.logWarning("Resumed/Rolled Back [" + suspended + "] transactions.", module);
            }
        } catch (GenericTransactionException e) {
            Debug.logWarning(e, module);
        }
        elapsedTime = System.currentTimeMillis() - startMillis;
    }

    @Override
    public long getRuntime() {
        return elapsedTime;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }
}
