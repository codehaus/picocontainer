package org.picocontainer.gems.monitors.prefuse;

import org.picocontainer.gems.monitors.ComponentDependencyMonitor.Dependency;

public interface ComponentDependencyListener {
    public void addDependency(Dependency dependency);
}
