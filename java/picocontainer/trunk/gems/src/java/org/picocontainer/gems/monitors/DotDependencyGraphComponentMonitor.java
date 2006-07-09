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

    private HashSet deps = new HashSet();

    public DotDependencyGraphComponentMonitor(ComponentMonitor delegate) {
        super(delegate);
    }

    public DotDependencyGraphComponentMonitor() {
    }

    public void instantiated(Constructor constructor, Object instantiated, Object[] injected, long duration) {

        for (int i = 0; i < injected.length; i++) {
            String entry = "  " + instantiated.getClass().getName() + " -> " + injected[i].getClass().getName() + ";\n";
            deps.add(entry);
        }

        super.instantiated(constructor, instantiated, injected, duration);    //To change body of overridden methods use File | Settings | File Templates.
    }


    public String getClassDependencyGraph() {

        ArrayList list = new ArrayList(deps);
        Collections.sort(list);

        String dependencies = "";
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            String dep = (String) iterator.next();
            dependencies = dependencies + dep;
        }
        return dependencies;
    }
}
