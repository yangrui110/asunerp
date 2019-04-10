
package com.hanlin.fadp.widget.artifact;

import java.util.HashSet;
import java.util.Set;

/**
 * An object used for gathering artifact information.
 */
public final class ArtifactInfoContext {

    private final Set<String> entityNameSet = new HashSet<String>();
    private final Set<String> serviceNameSet = new HashSet<String>();
    private final Set<String> screenLocationSet = new HashSet<String>();
    private final Set<String> formLocationSet = new HashSet<String>();
    private final Set<String> requestLocationSet = new HashSet<String>();
    private final Set<String> targetLocationSet = new HashSet<String>();

    /**
     * Adds an entity name to this context.
     * @param name The entity name to add to this context
     */
    public void addEntityName(String name) {
        if (name != null) {
            this.entityNameSet.add(name);
        }
    }

    /**
     * Adds a form location to this context.
     * @param name The form location to add to this context
     */
    public void addFormLocation(String name) {
        if (name != null) {
            this.formLocationSet.add(name);
        }
    }

    /**
     * Adds a request location to this context.
     * @param name The request location to add to this context
     */
    public void addRequestLocation(String name) {
        if (name != null) {
            this.requestLocationSet.add(name);
        }
    }

    /**
     * Adds a screen location to this context.
     * @param name The screen location to add to this context
     */
    public void addScreenLocation(String name) {
        if (name != null) {
            this.screenLocationSet.add(name);
        }
    }

    /**
     * Adds a service name to this context.
     * @param name The service name to add to this context
     */
    public void addServiceName(String name) {
        if (name != null) {
            this.serviceNameSet.add(name);
        }
    }

    /**
     * Adds a target location to this context.
     * @param name The target location to add to this context
     */
    public void addTargetLocation(String name) {
        if (name != null) {
            this.targetLocationSet.add(name);
        }
    }

    /**
     * Returns the entity names in this context.
     * @return The entity names in this context
     */
    public Set<String> getEntityNames() {
        return this.entityNameSet;
    }

    /**
     * Returns the form locations in this context.
     * @return The form locations in this context
     */
    public Set<String> getFormLocations() {
        return this.formLocationSet;
    }

    /**
     * Returns the request locations in this context.
     * @return The request locations in this context
     */
    public Set<String> getRequestLocations() {
        return this.requestLocationSet;
    }

    /**
     * Returns the screen locations in this context.
     * @return The screen locations in this context
     */
    public Set<String> getScreenLocations() {
        return this.screenLocationSet;
    }

    /**
     * Returns the service names in this context.
     * @return The service names in this context
     */
    public Set<String> getServiceNames() {
        return this.serviceNameSet;
    }

    /**
     * Returns the target locations in this context.
     * @return The target locations in this context
     */
    public Set<String> getTargetLocations() {
        return this.targetLocationSet;
    }
}
