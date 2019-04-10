
package com.hanlin.fadp.webapp.view;

/**
 * ViewHandler - View Handler Interface
 */
public abstract class AbstractViewHandler implements ViewHandler {
    protected String name = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
