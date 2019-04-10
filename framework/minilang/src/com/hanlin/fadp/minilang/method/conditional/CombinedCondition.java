
package com.hanlin.fadp.minilang.method.conditional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.minilang.MiniLangElement;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Implements the &lt;and&gt;, &lt;or&gt;, &lt;not&gt;, and &lt;xor&gt; elements.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-Conditional%2FLoopingStatements">Mini-language Reference</a>
 */
public abstract class CombinedCondition extends MiniLangElement implements Conditional {

    protected final List<Conditional> subConditions;

    public CombinedCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        List<? extends Element> childElements = UtilXml.childElementList(element);
        if (MiniLangValidate.validationOn() && childElements.isEmpty()) {
            MiniLangValidate.handleError("No conditional elements.", simpleMethod, element);
        }
        List<Conditional> conditionalList = new ArrayList<Conditional>(childElements.size());
        for (Element conditionalElement : UtilXml.childElementList(element)) {
            conditionalList.add(ConditionalFactory.makeConditional(conditionalElement, simpleMethod));
        }
        this.subConditions = Collections.unmodifiableList(conditionalList);
    }

    protected void prettyPrint(StringBuilder messageBuffer, MethodContext methodContext, String combineText) {
        messageBuffer.append("(");
        for (Conditional subCond : subConditions) {
            subCond.prettyPrint(messageBuffer, methodContext);
            messageBuffer.append(combineText);
        }
        messageBuffer.append(")");
    }

    /**
     * A &lt;and&gt; element factory. 
     */
    public static final class AndConditionFactory extends ConditionalFactory<CombinedCondition> {
        @Override
        public CombinedCondition createCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CombinedCondition(element, simpleMethod) {
                @Override
                public boolean checkCondition(MethodContext methodContext) throws MiniLangException {
                    if (subConditions.size() == 0)
                        return true;
                    for (Conditional subCond : subConditions) {
                        if (!subCond.checkCondition(methodContext)) {
                            return false;
                        }
                    }
                    return true;
                }
                @Override
                public void prettyPrint(StringBuilder messageBuffer, MethodContext methodContext) {
                    prettyPrint(messageBuffer, methodContext, " AND ");
                }
            };
        }

        @Override
        public String getName() {
            return "and";
        }
    }

    /**
     * A &lt;not&gt; element factory. 
     */
    public static final class NotConditionFactory extends ConditionalFactory<CombinedCondition> {
        @Override
        public CombinedCondition createCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CombinedCondition(element, simpleMethod) {
                @Override
                public boolean checkCondition(MethodContext methodContext) throws MiniLangException {
                    if (subConditions.size() == 0)
                        return true;
                    Conditional subCond = subConditions.get(0);
                    return !subCond.checkCondition(methodContext);
                }
                @Override
                public void prettyPrint(StringBuilder messageBuffer, MethodContext methodContext) {
                    messageBuffer.append("( NOT ");
                    if (subConditions.size() > 0) {
                        Conditional subCond = subConditions.get(0);
                        subCond.prettyPrint(messageBuffer, methodContext);
                    }
                    messageBuffer.append(")");
                }
            };
        }

        @Override
        public String getName() {
            return "not";
        }
    }

    /**
     * A &lt;or&gt; element factory. 
     */
    public static final class OrConditionFactory extends ConditionalFactory<CombinedCondition> {
        @Override
        public CombinedCondition createCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CombinedCondition(element, simpleMethod) {
                @Override
                public boolean checkCondition(MethodContext methodContext) throws MiniLangException {
                    if (subConditions.size() == 0)
                        return true;
                    for (Conditional subCond : subConditions) {
                        if (subCond.checkCondition(methodContext)) {
                            return true;
                        }
                    }
                    return false;
                }
                @Override
                public void prettyPrint(StringBuilder messageBuffer, MethodContext methodContext) {
                    prettyPrint(messageBuffer, methodContext, " OR ");
                }
            };
        }

        @Override
        public String getName() {
            return "or";
        }
    }

    /**
     * A &lt;xor&gt; element factory. 
     */
    public static final class XorConditionFactory extends ConditionalFactory<CombinedCondition> {
        @Override
        public CombinedCondition createCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CombinedCondition(element, simpleMethod) {
                @Override
                public boolean checkCondition(MethodContext methodContext) throws MiniLangException {
                    if (subConditions.size() == 0)
                        return true;
                    boolean trueFound = false;
                    for (Conditional subCond : subConditions) {
                        if (subCond.checkCondition(methodContext)) {
                            if (trueFound) {
                                return false;
                            } else {
                                trueFound = true;
                            }
                        }
                    }
                    return trueFound;
                }
                @Override
                public void prettyPrint(StringBuilder messageBuffer, MethodContext methodContext) {
                    prettyPrint(messageBuffer, methodContext, " XOR ");
                }
            };
        }

        @Override
        public String getName() {
            return "xor";
        }
    }
}
