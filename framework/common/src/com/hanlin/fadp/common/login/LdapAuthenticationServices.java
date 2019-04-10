

package com.hanlin.fadp.common.login;

import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.transaction.Transaction;

import com.hanlin.fadp.base.crypto.HashCrypt;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.transaction.GenericTransactionException;
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.entity.util.EntityUtilProperties;
import com.hanlin.fadp.service.DispatchContext;

/** LDAP Authentication Services.
 */
public class LdapAuthenticationServices {

    public static final String module = LdapAuthenticationServices.class.getName();

    public static boolean userLogin(DispatchContext ctx, Map<String, ?> context) {
        Debug.logVerbose("Starting LDAP authentication", module);
        Properties env = UtilProperties.getProperties("jndiLdap");
        String username = (String) context.get("login.username");
        if (username == null) {
            username = (String) context.get("username");
        }
        String password = (String) context.get("login.password");
        if (password == null) {
            password = (String) context.get("password");
        }
        String dn = null;
        Delegator delegator = ctx.getDelegator();
        boolean isServiceAuth = context.get("isServiceAuth") != null && ((Boolean) context.get("isServiceAuth")).booleanValue();
        GenericValue userLogin = null;
        try {
            userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", username).cache(isServiceAuth).queryOne();
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
        }
        if (userLogin != null) {
            dn = userLogin.getString("userLdapDn");
        }
        if (UtilValidate.isEmpty(dn)) {
            String dnTemplate = (String) env.get("ldap.dn.template");
            if (dnTemplate != null) {
                dn = dnTemplate.replace("%u", username);
            }
            Debug.logVerbose("Using DN template: " + dn, module);
        } else {
            Debug.logVerbose("Using UserLogin.userLdapDn: " + dn, module);
        }
        env.put(Context.SECURITY_PRINCIPAL, dn);
        env.put(Context.SECURITY_CREDENTIALS, password);
        try {
            // Create initial context
            DirContext ldapCtx = new InitialDirContext(env);
            ldapCtx.close();
        } catch (NamingException e) {
            Debug.logVerbose("LDAP authentication failed: " + e.getMessage(), module);
            return false;
        }
        Debug.logVerbose("LDAP authentication succeeded", module);
        if (!"true".equals(env.get("ldap.synchronize.passwords"))) {
            return true;
        }
        // Synchronize user's OFBiz password with user's LDAP password
        if (userLogin != null) {
            boolean useEncryption = "true".equals(EntityUtilProperties.getPropertyValue("security.properties", "password.encrypt", delegator));
            String currentPassword = userLogin.getString("currentPassword");
            boolean samePassword;
            if (useEncryption) {
                samePassword = HashCrypt.comparePassword(currentPassword, LoginServices.getHashType(), password);
            } else {
                samePassword = currentPassword.equals(password);
            }
            if (!samePassword) {
                Debug.logVerbose("Starting password synchronization", module);
                userLogin.set("currentPassword", useEncryption ? HashCrypt.cryptUTF8(LoginServices.getHashType(), null, password) : password, false);
                Transaction parentTx = null;
                boolean beganTransaction = false;
                try {
                    try {
                        parentTx = TransactionUtil.suspend();
                    } catch (GenericTransactionException e) {
                        Debug.logError(e, "Could not suspend transaction: " + e.getMessage(), module);
                    }
                    try {
                        beganTransaction = TransactionUtil.begin();
                        userLogin.store();
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Error saving UserLogin", module);
                        try {
                            TransactionUtil.rollback(beganTransaction, "Error saving UserLogin", e);
                        } catch (GenericTransactionException e2) {
                            Debug.logError(e2, "Could not rollback nested transaction: " + e2.getMessage(), module);
                        }
                    } finally {
                        try {
                            TransactionUtil.commit(beganTransaction);
                            Debug.logVerbose("Password synchronized", module);
                        } catch (GenericTransactionException e) {
                            Debug.logError(e, "Could not commit nested transaction: " + e.getMessage(), module);
                        }
                    }
                } finally {
                    if (parentTx != null) {
                        try {
                            TransactionUtil.resume(parentTx);
                            Debug.logVerbose("Resumed the parent transaction.", module);
                        } catch (GenericTransactionException e) {
                            Debug.logError(e, "Could not resume parent nested transaction: " + e.getMessage(), module);
                        }
                    }
                }
            }
        }
        return true;
    }
}
