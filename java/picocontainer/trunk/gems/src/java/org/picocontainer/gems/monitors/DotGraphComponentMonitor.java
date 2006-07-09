package org.picocontainer.gems.monitors;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.defaults.DelegatingComponentMonitor;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class DotGraphComponentMonitor extends DelegatingComponentMonitor implements ComponentMonitor {

    Set deps = new HashSet();

    public DotGraphComponentMonitor(ComponentMonitor delegate) {
        super(delegate);
    }

    public DotGraphComponentMonitor() {
    }

    public void instantiated(Constructor constructor, Object instantiated, Object[] injected, long duration) {

        for (int i = 0; i < injected.length; i++) {
            String entry = "  " + instantiated.getClass().getName() + " -> " + injected[i].getClass().getName() + ";\n";
            deps.add(entry);
        }

        super.instantiated(constructor, instantiated, injected, duration);    //To change body of overridden methods use File | Settings | File Templates.
    }


    public String getClassDependencyGraph() {
        String dependencies = "";
        for (Iterator iterator = deps.iterator(); iterator.hasNext();) {
            String dep = (String) iterator.next();
            dependencies = dependencies + dep;
        }
        return dependencies;
    }
}
