package org.nanocontainer.nanoweb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Dispatcher {
    void dispatch(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String actionPath, String result);
}