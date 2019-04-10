
package com.hanlin.fadp.service.job;

import java.io.Serializable;
import java.util.List;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;

/**
 * Purge job - removes a JobSandbox entity value and its related values.
 */
@SuppressWarnings("serial")
public class PurgeJob extends AbstractJob implements Serializable {

    public static final String module = PurgeJob.class.getName();

    private final GenericValue jobValue;

    public PurgeJob(GenericValue jobValue) {
        super(jobValue.getString("jobId"), "Purge " + jobValue.getString("jobName"));
        this.jobValue = jobValue;
    }

    @Override
    public void exec() throws InvalidJobException {
        if (currentState != State.QUEUED) {
            throw new InvalidJobException("Illegal state change");
        }
        currentState = State.RUNNING;
        try {
            // TODO: This might need to be in a transaction - to avoid the possibility of
            // leaving orphaned related values.
            jobValue.remove();
            GenericValue relatedValue = jobValue.getRelatedOne("RecurrenceInfo", false);
            if (relatedValue != null) {
                List<GenericValue> valueList = relatedValue.getRelated("JobSandbox", null, null, false);
                if (valueList.isEmpty()) {
                    relatedValue.removeRelated("RecurrenceRule");
                    relatedValue.remove();
                }
            }
            relatedValue = jobValue.getRelatedOne("RuntimeData", false);
            if (relatedValue != null) {
                List<GenericValue> valueList = relatedValue.getRelated("JobSandbox", null, null, false);
                if (valueList.isEmpty()) {
                    relatedValue.remove();
                }
            }
            Debug.logInfo("Purged job " + getJobId(), module);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Exception thrown while purging job: ", module);
        }
    }

    @Override
    public boolean isValid() {
        return currentState == State.CREATED;
    }

    @Override
    public void deQueue() throws InvalidJobException {
        if (currentState != State.QUEUED) {
            throw new InvalidJobException("Illegal state change");
        }
    }
}
