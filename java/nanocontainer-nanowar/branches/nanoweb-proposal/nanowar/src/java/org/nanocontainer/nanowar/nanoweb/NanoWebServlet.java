package org.nanocontainer.nanoweb;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nanocontainer.nanowar.ServletContainerFinder;
import org.picocontainer.PicoContainer;

/**
 * @version $Id: NanoWebServlet.java 5 2005-05-17 03:23:21Z juze $
 */
public class NanoWebServlet extends HttpServlet {

    private static final long serialVersionUID = 3257844398468446514L;

    private final transient ServletContainerFinder containerFinder = new ServletContainerFinder();

    protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        PicoContainer nanowarContainer = containerFinder.findContainer(httpServletRequest);

        NanoWebServletComponent component = (NanoWebServletComponent) nanowarContainer.getComponentInstanceOfType(NanoWebServletComponent.class);

        if (component == null) {
            throw new ServletException("NanoWebServletComponent not found. Please, check your nanowar configuration.");
        }

        component.service(httpServletRequest, httpServletResponse);
    }

}
