package org.nanocontainer.nanoweb;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.servlet.VelocityServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NanoWebVelocityServlet extends VelocityServlet {
    private VelocityEngine velocityEngine = new VelocityEngine();

    protected void initVelocity(ServletConfig servletConfig) throws ServletException {
        Properties properties = new Properties();
        properties.setProperty( "resource.loader", "classpath");
        properties.setProperty( "classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        try {
            velocityEngine.init(properties);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected Template handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Context context) throws Exception {
        context.put("action", httpServletRequest.getAttribute("action"));
        String servletPath = NanoWebServlet.getServletPath(httpServletRequest);
        return velocityEngine.getTemplate(servletPath);
    }
}