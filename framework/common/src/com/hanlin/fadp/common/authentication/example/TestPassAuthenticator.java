

package com.hanlin.fadp.common.authentication.example;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.common.authentication.api.AuthenticatorException;

/**
 * TestPassAuthenticator
 */
public class TestPassAuthenticator extends TestFailAuthenticator {

    private static final String module = TestPassAuthenticator.class.getName();

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
    @Override
    public boolean authenticate(String username, String password, boolean isServiceAuth) throws AuthenticatorException {
        Debug.logInfo(this.getClass().getName() + " Authenticator authenticate() -- returning false", module);
        return true;
    }

    /**
     * Flag to test if this Authenticator is enabled
     *
     * @return true if the Authenticator is enabled
     */
    @Override
    public boolean isEnabled() {
        return false;
    }
}
