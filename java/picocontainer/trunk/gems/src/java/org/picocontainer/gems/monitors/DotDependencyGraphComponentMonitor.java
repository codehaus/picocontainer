/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.gems.monitors;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.defaults.DelegatingComponentMonitor;

import java.lang.reflect.Constructor;
import java.util.*;

public class DotDependencyGraphComponentMonitor extends DelegatingComponentMonitor implements ComponentMonitor {

    ArrayList instantiated = new ArrayList();

    public DotDependencyGraphComponentMonitor(ComponentMonitor delegate) {
        super(delegate);
    }

    public DotDependencyGraphComponentMonitor() {
    }

    public void instantiated(Constructor constructor, Object instantiated, Object[] injected, long duration) {

        this.instantiated.add(new Instantiation(constructor, instantiated, injected, duration));

        super.instantiated(constructor, instantiated, injected, duration);
    }


    public String getClassDependencyGraph() {

        HashSet deps = new HashSet();

        for (int i = 0; i < instantiated.size(); i++) {
            Instantiation instantiation = (Instantiation) instantiated.get(i);
            for (int j = 0; j < instantiation.getInjected().length; j++) {
                String entry = "  " + instantiation.getInstantiated().getClass().getName() + " -> " + instantiation.getInjected()[j].getClass().getName() + ";\n";
                deps.add(entry);
            }
        }

        ArrayList list = new ArrayList(deps);
        Collections.sort(list);

        String dependencies = "";
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            String dep = (String) iterator.next();
            dependencies = dependencies + dep;
        }
        return dependencies;
    }

    private static class Instantiation {
        Constructor constructor;
        Object instantiated;
        Object[] injected;
        long duration;
        public Instantiation(Constructor constructor, Object instantiated, Object[] injected, long duration) {
            this.constructor = constructor;
            this.instantiated = instantiated;
            this.injected = injected;
            this.duration = duration;
        }
        public Object getInstantiated() {
            return instantiated;
        }
        public Object[] getInjected() {
            return injected;
        }
    }
}
