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

import picocontainer.Container;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;

import nanocontainer.aggregating.reflect.SequentialInvocationHandler;


public class AggregatingNanoContainer implements Container {

    private final InvocationHandler invocationHandler;
    /** Cached */
    private Object proxy;
    private final Object[] componentsToAggregate;

    public AggregatingNanoContainer(Object[] componentsToAggregate,
                                    InvocationHandler invocationHandler) {
        this.componentsToAggregate = componentsToAggregate;
        this.invocationHandler = invocationHandler;
    }

    public static class ComponentsFromContainer extends AggregatingNanoContainer {
        public ComponentsFromContainer(Container containerToAggregateComponentsFor, InvocationHandler invocationHandler) {
            super(containerToAggregateComponentsFor.getComponents(), invocationHandler);
        }
    }

    public static class Default extends AggregatingNanoContainer {
        public Default(Container containerToAggregateComponentsFor) {
            super(containerToAggregateComponentsFor.getComponents(),
                    new SequentialInvocationHandler(containerToAggregateComponentsFor));
        }
    }
    
    public boolean hasComponent(Class aClass) {
        return true;
    }

    public Object getComponent(Class aClass) {
        return proxy;
    }

    public Object[] getComponents() {
        return new Object[]{proxy};
    }

    public Object getProxy() {

        if (proxy == null) {

            // TODO should fail if container isn't started. Throw checked exception?
            Set interfaces = new HashSet();
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
