package org.nanocontainer.nanoweb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Interceptor {

    public Object before(Object action, HttpServletRequest req, HttpServletResponse res) throws Exception;

    public Object after(Object action, Object result, HttpServletRequest req, HttpServletResponse res) throws Exception;
    
}
