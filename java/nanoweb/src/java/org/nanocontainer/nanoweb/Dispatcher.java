package org.nanocontainer.nanoweb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.net.MalformedURLException;
import java.io.IOException;

public interface Dispatcher {
    void dispatch(ServletContext servletContext, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String scriptPathWithoutExtension, String actionMethod, String result) throws IOException, ServletException;
}