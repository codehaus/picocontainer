/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 25-Feb-2004
 * Time: 13:18:49
 */
package org.nanocontainer.nanoweb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Dispatcher {
    void dispatch(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String actionPath, String result);
}