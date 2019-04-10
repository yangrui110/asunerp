
package com.hanlin.fadp.minilang.method.entityops;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.entity.transaction.GenericTransactionException;
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;transaction-rollback&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Ctransactionrollback%3E}}">Mini-language Reference</a>
 */
public final class TransactionRollback extends MethodOperation {

    public static final String module = TransactionRollback.class.getName();

    private final FlexibleMapAccessor<Boolean> beganTransactionFma;

    public TransactionRollback(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "began-transaction-name");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "began-transaction-name");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        beganTransactionFma = FlexibleMapAccessor.getInstance(MiniLangValidate.checkAttribute(element.getAttribute("began-transaction-name"), "beganTransaction"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        boolean beganTransaction = false;
        Boolean beganTransactionBoolean = beganTransactionFma.get(methodContext.getEnvMap());
        if (beganTransactionBoolean != null) {
            beganTransaction = beganTransactionBoolean.booleanValue();
        }
        try {
            TransactionUtil.rollback(beganTransaction, "Explicit rollback in simple-method [" + this.simpleMethod.getShortDescription() + "]", null);
        } catch (GenericTransactionException e) {
            String errMsg = "Exception thrown while rolling back transaction: " + e.getMessage();
            Debug.logWarning(e, errMsg, module);
            simpleMethod.addErrorMessage(methodContext, errMsg);
            return false;
        }
        beganTransactionFma.remove(methodContext.getEnvMap());
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<transaction-rollback ");
        sb.append("began-transaction-name=\"").append(this.beganTransactionFma).append("\" />");
        return sb.toString();
    }

    /**
     * A factory for the &lt;transaction-rollback&gt; element.
     */
    public static final class TransactionRollbackFactory implements Factory<TransactionRollback> {
        @Override
        public TransactionRollback createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new TransactionRollback(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "transaction-rollback";
        }
    }
}
