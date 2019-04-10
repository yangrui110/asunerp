

package com.hanlin.fadp.common.authentication.example;

import com.hanlin.fadp.common.authentication.api.Authenticator;
import com.hanlin.fadp.common.authentication.api.AuthenticatorException;
import com.hanlin.fadp.service.LocalDispatcher;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.base.util.Debug;

/**
 * LocalAuthenticator
 */
public class TestFailAuthenticator implements Authenticator {

    private static final String module = TestFailAuthenticator.class.getName();
    protected Delegator delegator;
    protected LocalDispatcher dispatcher;
    protected float weight = 1;

    /**
     * Method called when authenticator is first initialized (the delegator
     * object can be obtained from the LocalDispatcher)
     *
     * @param dispatcher The LocalDispatcher to use for this Authenticator
     */
    public void initialize(LocalDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.delegator = dispatcher.getDelegator();
        Debug.logInfo(this.getClass().getName() + " Authenticator initialized", module);
    }

    /**
     * Method to authenticate a user
     *
     * @param username      User's username
     * @param password      User's password
     * @param isServiceAuth true if authentication is for a service call
     * @return true if the user is authenticated
     * @throws com.hanlin.fadp.common.authentication.api.AuthenticatorException
     *          when a fatal error occurs during authentication
     */
    public boolean authenticate(String username, String password, boolean isServiceAuth) throws AuthenticatorException {
        Debug.logInfo(this.getClass().getName() + " Authenticator authenticate() -- returning false", module);
        return false;
    }

    /**
     * Logs a user out
     *
     * @param username User's username
     * @throws com.hanlin.fadp.common.authentication.api.AuthenticatorException
     *          when logout fails
     */
    public void logout(String username) throws AuthenticatorException {
        Debug.logInfo(this.getClass().getName() + " Authenticator logout()", module);
    }

    /**
     * Reads user information and syncs it to OFBiz (i.e. UserLogin, Person, etc)
     *
     * @param username User's username
     * @throws com.hanlin.fadp.common.authentication.api.AuthenticatorException
     *          user synchronization fails
     */
    public void syncUser(String username) throws AuthenticatorException {
        Debug.logInfo(this.getClass().getName() + " Authenticator syncUser()", module);
        // no user info to sync
    }

    /**
     * Updates a user's password
     *
     * @param username    User's username
     * @param password    User's current password
     * @param newPassword User's new password
     * @throws com.hanlin.fadp.common.authentication.api.AuthenticatorException
     *          when update password fails
     */
    public void updatePassword(String username, String password, String newPassword) throws AuthenticatorException {
        Debug.logInfo(this.getClass().getName() + " Authenticator updatePassword()", module);
    }

    /**
     * Weight of this authenticator (lower weights are run first)
     *
     * @return the weight of this Authenticator
     */
    public float getWeight() {
        return 1;
    }

    /**
     * Is the user synchronized back to OFBiz
     *
     * @return true if the user record is copied to the OFB database
     */
    public boolean isUserSynchronized() {
        Debug.logInfo(this.getClass().getName() + " Authenticator isUserSynchronized()", module);
        return true;
    }

    /**
     * Is this expected to be the only authenticator, if so errors will be thrown when users cannot be found
     *
     * @return true if this is expected to be the only Authenticator
     */
    public boolean isSingleAuthenticator() {
        Debug.logInfo(this.getClass().getName() + " Authenticator isSingleAuthenticator()", module);
        return false;
    }

    /**
     * Flag to test if this Authenticator is enabled
     *
     * @return true if the Authenticator is enabled
     */
    public boolean isEnabled() {
        return false;
    }
}
