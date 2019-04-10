

package com.hanlin.fadp.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.hanlin.fadp.base.concurrent.ExecutionPool;
import com.hanlin.fadp.base.container.Container;
import com.hanlin.fadp.base.container.ContainerConfig;
import com.hanlin.fadp.base.container.ContainerException;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.StringUtil;

public class DelegatorContainer implements Container {
    private String name;
    private List<String> preloadedDelegatorNames;

    @Override
    public void init(String[] args, String name, String configFile) throws ContainerException {
        this.name = name;

        ContainerConfig.Container cc = ContainerConfig.getContainer(name, configFile);

        preloadedDelegatorNames = StringUtil.split(ContainerConfig.getPropertyValue(cc, "preloaded-delegators", "default"), ", ");
    }

    @Override
    public boolean start() throws ContainerException {
        if (UtilValidate.isEmpty(preloadedDelegatorNames)) {
            return true;
        }
        List<Future<Delegator>> futures = new ArrayList<Future<Delegator>>();
        for (String preloadedDelegatorName: preloadedDelegatorNames) {
            futures.add(DelegatorFactory.getDelegatorFuture(preloadedDelegatorName));
        }
        ExecutionPool.getAllFutures(futures);
        return true;
    }

    @Override
    public void stop() throws ContainerException {
    }

    @Override
    public String getName() {
        return name;
    }
}
