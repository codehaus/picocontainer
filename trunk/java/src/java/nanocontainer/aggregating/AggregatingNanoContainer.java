/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy                                           *
 *****************************************************************************/

package nanocontainer.aggregating;

import picocontainer.PicoContainer;
import picocontainer.aggregated.AggregatedContainersContainer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;


/**
 * This class
 */
public class AggregatingNanoContainer extends AggregatedContainersContainer.Filter {

    private InvocationHandler invocationHandler;

    /** Cached */
    private Object proxy;

    public AggregatingNanoContainer(PicoContainer containerToAggregateComponentsFor,
                                    InvocationHandler invocationHandler) {
        super(containerToAggregateComponentsFor);
        setInvocationHandler(invocationHandler);
    }

    public static class Default extends AggregatingNanoContainer {
        public Default(PicoContainer containerToAggregateComponentsFor) {
            super(containerToAggregateComponentsFor, null);
        }
    }

    public void setInvocationHandler(InvocationHandler invocationHandler) {
        this.invocationHandler = invocationHandler;
    }

    public Object getProxy() {

        if (proxy == null) {

            // TODO should fail if container isn't started. Throw checked exception?
            Set interfaces = new HashSet();
            Object[] componentsToAggregate = getSubject().getComponents();
            for (int i = 0; i < componentsToAggregate.length; i++) {
                Class componentClass = componentsToAggregate[i].getClass();
                Class[] implemeted = componentClass.getInterfaces();
                List implementedList = Arrays.asList(implemeted);
                interfaces.addAll(implementedList);
            }

            Class[] interfaceArray = (Class[]) interfaces.toArray(new Class[interfaces.size()]);
            proxy = Proxy.newProxyInstance(getClass().getClassLoader(), interfaceArray, invocationHandler);
            return proxy;

        } else {
            return proxy;
        }
    }
}
