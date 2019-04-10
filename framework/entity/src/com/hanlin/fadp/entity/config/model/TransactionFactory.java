
package com.hanlin.fadp.entity.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.entity.GenericEntityConfException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;transaction-factory&gt;</code> element.
 *
 * @see <code>entity-config.xsd</code>
 */
@ThreadSafe
public final class TransactionFactory {

    private final String className; // type = xs:string
    private final UserTransactionJndi userTransactionJndi; // <user-transaction-jndi>
    private final TransactionManagerJndi transactionManagerJndi; // <transaction-manager-jndi>

    TransactionFactory(Element element) throws GenericEntityConfException {
        String lineNumberText = EntityConfig.createConfigFileLineNumberText(element);
        String className = element.getAttribute("class").intern();
        if (className.isEmpty()) {
            throw new GenericEntityConfException("<transaction-factory> element class attribute is empty" + lineNumberText);
        }
        this.className = className;
        Element userTransactionJndiElement = UtilXml.firstChildElement(element, "user-transaction-jndi");
        if (userTransactionJndiElement == null) {
            this.userTransactionJndi = null;
        } else {
            this.userTransactionJndi = new UserTransactionJndi(userTransactionJndiElement);
        }
        Element transactionManagerJndiElement = UtilXml.firstChildElement(element, "transaction-manager-jndi");
        if (transactionManagerJndiElement == null) {
            this.transactionManagerJndi = null;
        } else {
            this.transactionManagerJndi = new TransactionManagerJndi(transactionManagerJndiElement);
        }
    }

    /** Returns the value of the <code>class</code> attribute. */
    public String getClassName() {
        return this.className;
    }

    /** Returns the <code>&lt;user-transaction-jndi&gt;</code> child element, or <code>null</code> if no child element was found. */
    public UserTransactionJndi getUserTransactionJndi() {
        return this.userTransactionJndi;
    }

    /** Returns the <code>&lt;transaction-manager-jndi&gt;</code> child element, or <code>null</code> if no child element was found. */
    public TransactionManagerJndi getTransactionManagerJndi() {
        return this.transactionManagerJndi;
    }
}
