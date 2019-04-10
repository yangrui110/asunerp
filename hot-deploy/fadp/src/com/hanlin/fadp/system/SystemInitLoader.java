package com.hanlin.fadp.system;

import com.hanlin.fadp.base.start.Config;
import com.hanlin.fadp.base.start.StartupException;
import com.hanlin.fadp.base.start.StartupLoader;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.service.*;

/**
 * Created by cl on 2017/5/18.
 */
public class SystemInitLoader implements StartupLoader {
    public static final String module = SystemInitLoader.class.getName();

    @Override
    public void load(Config config, String[] args) throws StartupException {

    }

    @Override
    public void start() throws StartupException {
        String initServices = UtilProperties.getPropertyValue("system.properties", "init.services");
        if(UtilValidate.isNotEmpty(initServices)){
            String[] services = initServices.split(",");
            for(String serviceName:services){
                Delegator delegator=DelegatorFactory.getDelegator("default");
                LocalDispatcher dispatcher= ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(),delegator);
                try {
                    dispatcher.runSync(serviceName, null);
                    Debug.logInfo("启动项：【"+serviceName+"】执行成功",module);
                } catch (GenericServiceException e) {
                    Debug.logError(e,"启动服务执行失败",module);
                }
            }
        }
    }

    @Override
    public void unload() throws StartupException {

    }
}
