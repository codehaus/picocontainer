/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.struts2;

import org.picocontainer.web.PicoServletContainerListener;
import org.picocontainer.web.ThreadLocalLifecycleState;
import org.picocontainer.web.ScopedContainers;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Storing;
import org.picocontainer.behaviors.Guarding;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.Result;

import javax.servlet.ServletContextEvent;

import ognl.OgnlRuntime;

public class Struts2PicoServletContainerListener extends PicoServletContainerListener {

    public void contextInitialized(ServletContextEvent event) {
        OgnlRuntime.setSecurityManager(null);
        super.contextInitialized(event);
    }

    protected ScopedContainers makeScopedContainers(boolean stateless) {

        //NullLifecycleStrategy ls = new NullLifecycleStrategy();

        DefaultPicoContainer appCtnr = new DefaultPicoContainer(new Guarding().wrap(new Caching()), makeLifecycleStrategy(), makeParentContainer(), makeAppComponentMonitor());
        Storing sessStoring = new Storing();
        DefaultPicoContainer sessCtnr = new DefaultPicoContainer(new Guarding().wrap(sessStoring), makeLifecycleStrategy(), appCtnr, makeSessionComponentMonitor());
        Storing reqStoring = new Storing();
        DefaultPicoContainer reqCtnr = new DefaultPicoContainer(new Guarding().wrap(addRequestBehaviors(reqStoring)), makeLifecycleStrategy(), sessCtnr, makeRequestComponentMonitor());
        ThreadLocalLifecycleState sessionState = new ThreadLocalLifecycleState();
        ThreadLocalLifecycleState requestState = new ThreadLocalLifecycleState();
        sessCtnr.setLifecycleState(sessionState);
        reqCtnr.setLifecycleState(requestState);

        return new ScopedContainers(appCtnr, sessCtnr, reqCtnr, sessStoring, reqStoring, sessionState, requestState);

    }

    /**
     * Struts2 handles whole value objects in some configurations.
     * This enables lazy instantiation of them    
     */
    @Override
    protected ComponentMonitor makeRequestComponentMonitor() {
        return new StrutsActionInstantiatingComponentMonitor();
    }

    public static class StrutsActionInstantiatingComponentMonitor extends NullComponentMonitor {
        public Object noComponentFound(MutablePicoContainer mutablePicoContainer, Object o) {
            return noComponent(mutablePicoContainer, o);
        }

        private Object noComponent(MutablePicoContainer mutablePicoContainer, Object o) {
            if (o instanceof Class) {
                Class clazz = (Class) o;
                if (Action.class.isAssignableFrom(clazz) || Result.class.isAssignableFrom(clazz)) {
                    try {
                        mutablePicoContainer.addComponent(clazz);
                    } catch (NoClassDefFoundError e) {
                        if (e.getMessage().equals("org/apache/velocity/context/Context")) {
                            // half expected. XWork seems to setup stuff that cannot
                            // work
                            // TODO if this is the case we should make configurable
                            // the list of classes we "expect" not to find.  Odd!
                        } else {
                            throw e;
                        }
                    }

                    return null;
                }
                try {
                    if (clazz.getConstructor(new Class[0]) != null) {
                        return clazz.newInstance();
                    }
                } catch (InstantiationException e) {
                    throw new PicoCompositionException("can't instantiate " + o);
                } catch (IllegalAccessException e) {
                    throw new PicoCompositionException("illegal access " + o);
                } catch (NoSuchMethodException e) {
                }
            }
            return super.noComponentFound(mutablePicoContainer, o);
        }
    }
}
