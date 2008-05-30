/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.axis;

import javax.servlet.http.HttpServletRequest;

import org.apache.axis.MessageContext;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.cache.ClassCache;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.PicoServletContainerFilter;

/**
 * Axis provider for RPC-style services that uses the servlet container
 * hierarchy to instantiate service classes and resolve their dependencies.
 * 
 * @author <a href="mailto:evan@bottch.com">Evan Bottcher</a>
 */
public class NanoRPCProvider extends RPCProvider {

    protected Object makeNewServiceObject(MessageContext msgContext, String clsName) throws Exception {

        ClassLoader cl = msgContext.getClassLoader();
        ClassCache cache = msgContext.getAxisEngine().getClassCache();
        Class svcClass = cache.lookup(clsName, cl).getJavaClass();

        return instantiateService(svcClass, msgContext);

    }

    private Object instantiateService(Class svcClass, MessageContext msgContext) {

        HttpServletRequest request = (HttpServletRequest) msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
        MutablePicoContainer requestContainer = PicoServletContainerFilter.getRequestContainerForThread();

        MutablePicoContainer container = new DefaultPicoContainer(requestContainer);
        container.addComponent(svcClass);
        return container.getComponent(svcClass);
    }

}
