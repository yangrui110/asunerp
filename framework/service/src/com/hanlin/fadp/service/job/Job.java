
package com.hanlin.fadp.service.job;

import java.util.Date;

/**
 * A scheduled job.
 * <p>A job starts out in the created state. When the job is queued for execution, it
 * transitions to the queued state. While the job is executing it is in the running state.
 * When the job execution ends, it transitions to the finished or failed state - depending
 * on the outcome of the task that was performed.</p>
 */
public interface Job extends Runnable {

    public static enum State {CREATED, QUEUED, RUNNING, FINISHED, FAILED};

    /**
     * Returns the current state of this job.
     */
    State currentState();

    /**
     * Returns the ID of this Job.
     */
    String getJobId();

    /**
     * Returns the name of this Job.
     */
    String getJobName();

    /**
     *  Returns the job execution time in milliseconds.
     *  Returns zero if the job has not run.
     */
    long getRuntime();

    /**
     * Returns true if this job is ready to be queued.
     */
    boolean isValid();

    /**
     * Transitions this job to the pre-queued (created) state. The job manager
     * will call this method when there was a problem adding this job to the queue.
     */
    void deQueue() throws InvalidJobException;

    /**
     * Transitions this job to the queued state.
     */
    void queue() throws InvalidJobException;

    /**
     * Returns the time this job is scheduled to start.
     */
    Date getStartTime();
}

