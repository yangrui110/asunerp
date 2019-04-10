
package com.hanlin.fadp.minilang.method.ifops;

import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;if-not-empty&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cifnotempty%3E}}">Mini-language Reference</a>
 */
public final class IfNotEmpty extends MethodOperation {

    private final List<MethodOperation> elseSubOps;
    private final FlexibleMapAccessor<Object> fieldFma;
    private final List<MethodOperation> subOps;

    public IfNotEmpty(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        this.subOps = Collections.unmodifiableList(SimpleMethod.readOperations(element, simpleMethod));
        Element elseElement = UtilXml.firstChildElement(element, "else");
        if (elseElement != null) {
            this.elseSubOps = Collections.unmodifiableList(SimpleMethod.readOperations(elseElement, simpleMethod));
        } else {
            this.elseSubOps = null;
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Object fieldVal = fieldFma.get(methodContext.getEnvMap());
        if (!ObjectType.isEmpty(fieldVal)) {
            return SimpleMethod.runSubOps(subOps, methodContext);
        } else {
            if (elseSubOps != null) {
                return SimpleMethod.runSubOps(elseSubOps, methodContext);
            } else {
                return true;
            }
        }
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        for (MethodOperation method : this.subOps) {
            method.gatherArtifactInfo(aic);
        }
        if (this.elseSubOps != null) {
            for (MethodOperation method : this.elseSubOps) {
                method.gatherArtifactInfo(aic);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<if-not-empty ");
        sb.append("field=\"").append(this.fieldFma).append("\"/>");
        return sb.toString();
    }

    /**
     * A &lt;if-not-empty&gt; element factory. 
     */
    public static final class IfNotEmptyFactory implements Factory<IfNotEmpty> {
        @Override
        public IfNotEmpty createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new IfNotEmpty(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "if-not-empty";
        }
    }
}
