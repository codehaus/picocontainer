package org.picocontainer.gems.monitors.prefuse;

import java.util.HashSet;
import java.util.Set;

import org.picocontainer.gems.monitors.ComponentDependencyMonitor.Dependency;

public class DependencySet implements ComponentDependencyListener {

    private Set dependencies = new HashSet();
    private ComponentDependencyListener listener;

    public DependencySet(ComponentDependencyListener listener) {
        this.listener = listener;
    }
    
    public void addDependency(Dependency dependency) {
        if(dependencies.add(dependency)) {
            listener.addDependency(dependency);
        }
    }

    public Dependency[] getDependencies() {
        return (Dependency[]) dependencies.toArray(new Dependency[dependencies.size()]);
    }
}
