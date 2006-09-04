/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.webcontainer;

import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletHandler;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.servlet.Servlet;

public class PicoServletHolder extends ServletHolder {

    private final PicoContainer parentContainer;

    public PicoServletHolder(PicoContainer parentContainer) {
        this.parentContainer = parentContainer;
    }


    public PicoServletHolder(Class clazz, PicoContainer parentContainer) {
        super(clazz);
        this.parentContainer = parentContainer;
    }

    public synchronized Object newInstance() throws InstantiationException, IllegalAccessException {
        DefaultPicoContainer child = new DefaultPicoContainer(parentContainer);
        child.registerComponentImplementation(Servlet.class, _class);
//        System.out.println("--> newInstance()" + _class);
        return child.getComponentInstance(Servlet.class);
    }

//    public void setForcedPath(String forcedPath)
//    {
//        System.out.println("--> setForcedPath() " + forcedPath);
//        super.setForcedPath(forcedPath);
//    }
//
//    public void setInitOrder(int i) {
//        System.out.println("--> setInitOrder() " + i);
//        super.setInitOrder(i);
//    }
//
//    public void setInitParameter(String string, String string1) {
//        System.out.println("--> setInitParameter() " + string + " " + string1);
//        super.setInitParameter(string, string1);
//    }
//
//    public void setRunAs(String string) {
//        System.out.println("--> setRunAs() " + string);
//        super.setRunAs(string);
//    }
//
//    public void setServletHandler(ServletHandler servletHandler) {
//        System.out.println("--> setServletHandler() " + servletHandler.toString());
//        super.setServletHandler(servletHandler);
//    }


}