
package com.hanlin.fadp.service.engine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import com.hanlin.fadp.base.config.GenericConfigException;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilDateTime;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.serialize.SerializeException;
import com.hanlin.fadp.entity.serialize.XmlSerializer;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.GenericRequester;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;
import com.hanlin.fadp.service.config.ServiceConfigUtil;
import com.hanlin.fadp.service.job.GenericServiceJob;
import com.hanlin.fadp.service.job.Job;
import com.hanlin.fadp.service.job.JobManager;
import com.hanlin.fadp.service.job.JobManagerException;

/**
 * Generic Asynchronous Engine
 */
public abstract class GenericAsyncEngine extends AbstractEngine {

    public static final String module = GenericAsyncEngine.class.getName();

    protected GenericAsyncEngine(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runSync(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map)
     */
    public abstract Map<String, Object> runSync(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException;

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runSyncIgnore(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map)
     */
    public abstract void runSyncIgnore(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException;

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runAsync(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map, boolean)
     */
    public void runAsync(String localName, ModelService modelService, Map<String, Object> context, boolean persist) throws GenericServiceException {
        runAsync(localName, modelService, context, null, persist);
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runAsync(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map, com.hanlin.fadp.service.GenericRequester, boolean)
     */
    public void runAsync(String localName, ModelService modelService, Map<String, Object> context, GenericRequester requester, boolean persist) throws GenericServiceException {
        DispatchContext dctx = dispatcher.getLocalContext(localName);
        Job job = null;

        if (persist) {
            // check for a delegator
            if (dispatcher.getDelegator() == null) {
                throw new GenericServiceException("No reference to delegator; cannot run persisted services.");
            }

            GenericValue jobV = null;
            // Build the value object(s).
            try {
                // Create the runtime data
                String dataId = dispatcher.getDelegator().getNextSeqId("RuntimeData");

                GenericValue runtimeData = dispatcher.getDelegator().makeValue("RuntimeData", "runtimeDataId", dataId);

                runtimeData.set("runtimeInfo", XmlSerializer.serialize(context));
                runtimeData.create();

                // Get the userLoginId out of the context
                String authUserLoginId = null;
                if (context.get("userLogin") != null) {
                    GenericValue userLogin = (GenericValue) context.get("userLogin");
                    authUserLoginId = userLogin.getString("userLoginId");
                }

                // Create the job info
                String jobId = dispatcher.getDelegator().getNextSeqId("JobSandbox");
                String jobName = Long.toString(System.currentTimeMillis());

                Map<String, Object> jFields = UtilMisc.toMap("jobId", jobId, "jobName", jobName, "runTime", UtilDateTime.nowTimestamp());
                jFields.put("poolId", ServiceConfigUtil.getServiceEngine().getThreadPool().getSendToPool());
                jFields.put("statusId", "SERVICE_PENDING");
                jFields.put("serviceName", modelService.name);
                jFields.put("loaderName", localName);
                jFields.put("maxRetry", Long.valueOf(modelService.maxRetry));
                jFields.put("runtimeDataId", dataId);
                if (UtilValidate.isNotEmpty(authUserLoginId)) {
                    jFields.put("authUserLoginId", authUserLoginId);
                }

                jobV = dispatcher.getDelegator().makeValue("JobSandbox", jFields);
                jobV.create();
            } catch (GenericEntityException e) {
                throw new GenericServiceException("Unable to create persisted job", e);
            } catch (SerializeException e) {
                throw new GenericServiceException("Problem serializing service attributes", e);
            } catch (FileNotFoundException e) {
                throw new GenericServiceException("Problem serializing service attributes", e);
            } catch (IOException e) {
                throw new GenericServiceException("Problem serializing service attributes", e);
            } catch (GenericConfigException e) {
                throw new GenericServiceException("Problem serializing service attributes", e);
            }

            Debug.logInfo("Persisted job queued : " + jobV.getString("jobName"), module);
        } else {
            JobManager jMgr = dispatcher.getJobManager();
            if (jMgr != null) {
                String name = Long.toString(System.currentTimeMillis());
                String jobId = modelService.name + "." + name;
                job = new GenericServiceJob(dctx, jobId, name, modelService.name, context, requester);
                try {
                    dispatcher.getJobManager().runJob(job);
                } catch (JobManagerException jse) {
                    throw new GenericServiceException("Cannot run job.", jse);
                }
            } else {
                throw new GenericServiceException("Cannot get JobManager instance to invoke the job");
            }
        }
    }

    @Override
    protected boolean allowCallbacks(ModelService model, Map<String, Object> context, int mode) throws GenericServiceException {
        return mode == GenericEngine.SYNC_MODE;
    }
}
