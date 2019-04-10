
package com.hanlin.fadp.service.config;

import com.hanlin.fadp.service.config.model.ServiceConfig;

/**
 * An object that receives notifications when the <code>serviceengine.xml</code> file is reloaded.
 */
public interface ServiceConfigListener {

    /**
     * Processes <code>serviceengine.xml</code> changes.
     * 
     * @param serviceConfig The new <code>ServiceConfig</code> instance.
     */
    void onServiceConfigChange(ServiceConfig serviceConfig);

}
