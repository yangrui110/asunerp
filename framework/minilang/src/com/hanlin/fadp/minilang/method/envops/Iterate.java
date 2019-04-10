
package com.hanlin.fadp.minilang.method.envops;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityListIterator;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import com.hanlin.fadp.minilang.method.envops.Break.BreakElementException;
import com.hanlin.fadp.minilang.method.envops.Continue.ContinueElementException;
import org.w3c.dom.Element;

/**
 * Implements the &lt;iterate&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Citerate%3E}}">Mini-language Reference</a>
 */
public final class Iterate extends MethodOperation {

    public static final String module = Iterate.class.getName();

    private final FlexibleMapAccessor<Object> entryFma;
    private final FlexibleMapAccessor<Object> listFma;
    private final List<MethodOperation> subOps;

    public Iterate(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "entry", "list");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "entry", "list");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "entry", "list");
        }
        this.entryFma = FlexibleMapAccessor.getInstance(element.getAttribute("entry"));
        this.listFma = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
        this.subOps = Collections.unmodifiableList(SimpleMethod.readOperations(element, simpleMethod));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        if (listFma.isEmpty()) {
            if (Debug.verboseOn())
                Debug.logVerbose("Collection not found, doing nothing: " + this, module);
            return true;
        }
        Object oldEntryValue = entryFma.get(methodContext.getEnvMap());
        Object objList = listFma.get(methodContext.getEnvMap());
        if (objList instanceof EntityListIterator) {
            EntityListIterator eli = (EntityListIterator) objList;
            GenericValue theEntry;
            try {
                while ((theEntry = eli.next()) != null) {
                    entryFma.put(methodContext.getEnvMap(), theEntry);
                    try {
                        for (MethodOperation methodOperation : subOps) {
                            if (!methodOperation.exec(methodContext)) {
                                return false;
                            }
                        }
                    } catch (MiniLangException e) {
                        if (e instanceof BreakElementException) {
                            break;
                        }
                        if (e instanceof ContinueElementException) {
                            continue;
                        }
                        throw e;
                    }
                }
            } finally {
                try {
                    eli.close();
                } catch (GenericEntityException e) {
                    throw new MiniLangRuntimeException("Error closing entityListIterator: " + e.getMessage(), this);
                }
            }
        } else if (objList instanceof Collection<?>) {
            Collection<Object> theCollection = UtilGenerics.checkCollection(objList);
            if (theCollection.size() == 0) {
                if (Debug.verboseOn())
                    Debug.logVerbose("Collection has zero entries, doing nothing: " + this, module);
                return true;
            }
            for (Object theEntry : theCollection) {
                entryFma.put(methodContext.getEnvMap(), theEntry);
                try {
                    for (MethodOperation methodOperation : subOps) {
                        if (!methodOperation.exec(methodContext)) {
                            return false;
                        }
                    }
                } catch (MiniLangException e) {
                    if (e instanceof BreakElementException) {
                        break;
                    }
                    if (e instanceof ContinueElementException) {
                        continue;
                    }
                    throw e;
                }
            }
        } else if (objList instanceof Iterator<?>) {
            Iterator<Object> theIterator = UtilGenerics.cast(objList);
            if (!theIterator.hasNext()) {
                if (Debug.verboseOn())
                    Debug.logVerbose("Iterator has zero entries, doing nothing: " + this, module);
                return true;
            }
            while (theIterator.hasNext()) {
                Object theEntry = theIterator.next();
                entryFma.put(methodContext.getEnvMap(), theEntry);
                try {
                    for (MethodOperation methodOperation : subOps) {
                        if (!methodOperation.exec(methodContext)) {
                            return false;
                        }
                    }
                } catch (MiniLangException e) {
                    if (e instanceof BreakElementException) {
                        break;
                    }
                    if (e instanceof ContinueElementException) {
                        continue;
                    }
                    throw e;
                }
            }
        } else {
            if (Debug.verboseOn()) {
                Debug.logVerbose("Cannot iterate over a " + objList == null ? "null object" : objList.getClass().getName()
                        + ", doing nothing: " + this, module);
            }
            return true;
        }
        entryFma.put(methodContext.getEnvMap(), oldEntryValue);
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        for (MethodOperation method : this.subOps) {
            method.gatherArtifactInfo(aic);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<iterate ");
        if (!this.entryFma.isEmpty()) {
            sb.append("entry=\"").append(this.entryFma).append("\" ");
        }
        if (!this.listFma.isEmpty()) {
            sb.append("list=\"").append(this.listFma).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;iterate&gt; element.
     */
    public static final class IterateFactory implements Factory<Iterate> {
        @Override
        public Iterate createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new Iterate(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "iterate";
        }
    }
}
