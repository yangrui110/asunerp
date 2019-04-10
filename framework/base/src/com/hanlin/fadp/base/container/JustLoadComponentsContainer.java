
package com.hanlin.fadp.base.container;

import com.hanlin.fadp.base.component.AlreadyLoadedException;
import com.hanlin.fadp.base.component.ComponentException;
import com.hanlin.fadp.base.util.Debug;

/**
 * A Container implementation to run the tests configured through this testtools stuff.
 */
public class JustLoadComponentsContainer implements Container {

    public static final String module = JustLoadComponentsContainer.class.getName();

    private String name;

    @Override
    public void init(String[] args, String name, String configFile) {
        this.name = name;
        try {
            ComponentContainer cc = new ComponentContainer();
            cc.loadComponents(null);
        } catch (AlreadyLoadedException e) {
            Debug.logError(e, module);
        } catch (ComponentException e) {
            Debug.logError(e, module);
            //throw UtilMisc.initCause(new ContainerException(e.getMessage()), e);
        }
    }

    public boolean start() throws ContainerException {
        return true;
    }

    public void stop() throws ContainerException {
    }

    public String getName() {
        return name;
    }
}
