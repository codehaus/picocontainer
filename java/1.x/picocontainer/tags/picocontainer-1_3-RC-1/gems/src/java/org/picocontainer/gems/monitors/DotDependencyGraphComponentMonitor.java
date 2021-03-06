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

    ArrayList allInstantiated = new ArrayList();

    public DotDependencyGraphComponentMonitor(ComponentMonitor delegate) {
        super(delegate);
    }

    public DotDependencyGraphComponentMonitor() {
    }

    public void instantiated(Constructor constructor, Object instantiated, Object[] injected, long duration) {

        this.allInstantiated.add(new Instantiation(constructor, instantiated, injected, duration));

        super.instantiated(constructor, instantiated, injected, duration);
    }


    public String getClassDependencyGraph() {

        HashSet lines = new HashSet();

        for (int i = 0; i < allInstantiated.size(); i++) {
            Instantiation instantiation = (Instantiation) allInstantiated.get(i);
            for (int j = 0; j < instantiation.getInjected().length; j++) {
                Object instantiated = instantiation.getInstantiated();
                Object injected = instantiation.getInjected()[j];
                lines.add("  '" + instantiated.getClass().getName() + "' -> '" + injected.getClass().getName() + "';\n");
            }
        }

        return sortLines(lines);
    }

    private String sortLines(HashSet lines) {
        ArrayList list = new ArrayList(lines);
        Collections.sort(list);

        String dependencies = "";
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            String dep = (String) iterator.next();
            dependencies = dependencies + dep;
        }
        return dependencies.replaceAll("'","\"");
    }

    public String getInterfaceDependencyGraph() {
        HashSet lines = new HashSet();

        for (int i = 0; i < allInstantiated.size(); i++) {
            Instantiation instantiation = (Instantiation) allInstantiated.get(i);
            for (int j = 0; j < instantiation.getInjected().length; j++) {
                Object injected = instantiation.getInjected()[j];
                Class injectedType = instantiation.getConstructor().getParameterTypes()[j];
                Object instantiated = instantiation.getInstantiated();
                if (injected.getClass() != injectedType) {
                    lines.add("  '" + instantiated.getClass().getName() + "' -> '" + injectedType.getName() + "' [style=dotted,label='needs'];\n");
                    lines.add("  '" + injected.getClass().getName() + "' -> '" + injectedType.getName() + "' [style=dotted, color=red,label='isA'];\n");
                    lines.add("  '" + injectedType.getName() + "' [shape=box, label=" + printClassName(injectedType) + "];\n");
                } else {
                    lines.add("  '" + instantiated.getClass().getName() + "' -> '" + injected.getClass().getName() + "' [label='needs'];\n");
                }
                lines.add("  '" + instantiated.getClass().getName() + "' [label=" + printClassName(instantiated.getClass()) + "];\n");

            }
        }

        return sortLines(lines);
    }

    private String printClassName(Class clazz) {
        String className = clazz.getName();
        return "'" + className.substring(className.lastIndexOf(".")+1) + "\\n" + clazz.getPackage().getName() + "'";

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

        public Constructor getConstructor() {
            return constructor;
        }

        public Object getInstantiated() {
            return instantiated;
        }
        public Object[] getInjected() {
            return injected;
        }
    }
}
