
package com.hanlin.fadp.entity.finder;

import java.util.Map;
import java.io.Serializable;
import org.w3c.dom.Element;

import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.Delegator;

@SuppressWarnings("serial")
public abstract class Finder implements Serializable {
    protected FlexibleStringExpander entityNameExdr;
    protected FlexibleStringExpander useCacheStrExdr;

    protected Finder(Element element) {
        this.entityNameExdr = FlexibleStringExpander.getInstance(element.getAttribute("entity-name"));
        this.useCacheStrExdr = FlexibleStringExpander.getInstance(element.getAttribute("use-cache"));
    }

    public String getEntityName() {
        String entName = this.entityNameExdr.getOriginal();
        // if there is expansion syntax
        if (entName.indexOf("${") >= 0) return null;
        return entName;
    }

    public void setEntityName(String entityName) {
        this.entityNameExdr = FlexibleStringExpander.getInstance(entityName);
    }

    public abstract void runFind(Map<String, Object> context, Delegator delegator) throws GeneralException;
}

