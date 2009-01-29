/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.web.remoting;

import org.picocontainer.web.PicoServletContainerListener;
import org.picocontainer.web.StringFromRequest;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.containers.TransientPicoContainer;
import org.picocontainer.monitors.NullComponentMonitor;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Modifier;
import java.io.Serializable;

public class PWRServletContainerListener extends PicoServletContainerListener {

    protected ComponentMonitor makeRequestComponentMonitor() {
        return new LateInstantiatingComponentMonitor();
    }

    private static class LateInstantiatingComponentMonitor extends NullComponentMonitor implements Serializable {

        public Object noComponentFound(MutablePicoContainer mutablePicoContainer, Object componentKey) {
            if (componentKey instanceof Class) {
                Class clazz = (Class) componentKey;
                if (clazz.getName().startsWith("java.lang")) {
                    return null;
                }
                if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                    Object instance = new TransientPicoContainer(mutablePicoContainer)
                            .addComponent(clazz).getComponent(clazz);
                    if (instance != null) {
                        return instance;
                    }
                }
            } else if (componentKey instanceof String) {
                Object instance = new StringFromRequest((String) componentKey).provide(
                        mutablePicoContainer.getComponent(HttpServletRequest.class));
                if (instance != null) {
                    return instance;
                }
            }
            return null;
        }

    }
}