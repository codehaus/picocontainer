/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.type3msg;

import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class Type3MsgPicoContainer extends DefaultPicoContainer {
    private Map impls = new HashMap();

    public void registerMulticasted(Class theInterface, Object theImpl) {
        MulticastingProxy proxy = (MulticastingProxy) impls.get(theInterface);
        if (proxy == null) {
            proxy = new MulticastingProxy(theInterface);
            impls.put(theInterface, proxy);
            registerComponentInstance(theInterface, proxy.getProxy());
        }
        proxy.add(theImpl);
    }

    public void registerRoundRobin(Class theInterface, Object theImpl) {
        RoundRobinMulticastingProxy proxy = (RoundRobinMulticastingProxy) impls.get(theInterface);
        if (proxy == null) {
            proxy = new RoundRobinMulticastingProxy(theInterface);
            impls.put(theInterface, proxy);
            registerComponentInstance(theInterface, proxy.getProxy());
        }
        proxy.add(theImpl);
    }


    //proxy = new MulticastingProxy(theInterface, theCreationStratgy, theDelegationStrategy)
}
