
package com.hanlin.fadp.minilang.method.ifops;

import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;if-instance-of&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cifinstanceof%3E}}">Mini-language Reference</a>
 */
public final class IfInstanceOf extends MethodOperation {

    private final String className;
    private final Class<?> compareClass;
    private final List<MethodOperation> elseSubOps;
    private final FlexibleMapAccessor<Object> fieldFma;
    private final List<MethodOperation> subOps;

    public IfInstanceOf(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "field", "class");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field", "class");
            MiniLangValidate.constantAttributes(simpleMethod, element, "class");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field");
        }
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        this.className = element.getAttribute("class");
        Class<?> compareClass = null;
        if (!className.isEmpty()) {
            try {
                compareClass = ObjectType.loadClass(className);
            } catch (ClassNotFoundException e) {
                MiniLangValidate.handleError("Invalid class name " + className, simpleMethod, element);
            }
        }
        this.compareClass = compareClass;
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
        if (this.compareClass == null) {
            throw new MiniLangRuntimeException("Invalid class name " + className, this);
        }
        boolean runSubOps = false;
        Object fieldVal = fieldFma.get(methodContext.getEnvMap());
        if (fieldVal != null) {
            runSubOps = ObjectType.instanceOf(fieldVal.getClass(), compareClass);
        }
        if (runSubOps) {
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
        StringBuilder sb = new StringBuilder("<if-instance-of ");
        sb.append("field=\"").append(this.fieldFma).append("\" ");
        if (compareClass != null) {
            sb.append("class=\"").append(compareClass.getName()).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A &lt;if-instance-of&gt; element factory. 
     */
    public static final class IfInstanceOfFactory implements Factory<IfInstanceOf> {
        @Override
        public IfInstanceOf createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new IfInstanceOf(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "if-instance-of";
        }
    }
}
