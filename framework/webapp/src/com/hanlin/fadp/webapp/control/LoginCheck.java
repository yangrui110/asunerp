
package com.hanlin.fadp.webapp.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginCheck {
    boolean isEnabled();
    String associate(HttpServletRequest request, HttpServletResponse response);
    String check(HttpServletRequest request, HttpServletResponse response);
}
