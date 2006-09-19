package org.picocontainer.gems.monitors;

import java.lang.reflect.Constructor;

import org.picocontainer.defaults.DelegatingComponentMonitor;
import org.picocontainer.gems.monitors.prefuse.ComponentDependencyListener;

/**
 * Understands how to capture component dependency information from picocontainer.
 * 
 * @author Peter Barry
 * @author Kent R. Spillner
 */
public class ComponentDependencyMonitor extends DelegatingComponentMonitor {

    private final ComponentDependencyListener listener;

    public ComponentDependencyMonitor(ComponentDependencyListener listener) {
        this.listener = listener;
    }

    public void instantiated(Constructor constructor, Object instantiated, Object[] injected, long duration) {
        int count = injected.length;

        if (count == 0) {
            listener.addDependency(new Dependency(instantiated.getClass(), null));
        }
        
        for (int i = 0; i < count; i++) {
            Object dependent = injected[i];
            Dependency dependency = new Dependency(instantiated.getClass(), dependent.getClass());
            listener.addDependency(dependency);
        }
    }

    /**
     * Understands which other classes are required to instantiate a component.
     * 
     * @author Peter Barry
     * @author Kent R. Spillner
     */
    public static class Dependency {

        private Class component;

        private Class dependency;

        public Dependency(Class componentType, Class dependencyType) {
            this.component = componentType;
            this.dependency = dependencyType;
        }

        public boolean dependsOn(Class dependencyType) {
            return dependencyType == null ? false : dependency.equals(dependencyType);
        }

        public boolean equals(Object other) {
            if (other != null && other instanceof Dependency) {
                Dependency otherDependency = (Dependency) other;
                return areEqualOrNull(component, otherDependency.component)
                        && areEqualOrNull(dependency, otherDependency.dependency);
            }
            return false;
        }

        public Class getComponent() {
            return component;
        }

        public Class getDependency() {
            return dependency;
        }

        public String toString() {
            return component + " depends on " + dependency;
        }

        private static boolean areEqualOrNull(Class type, Class otherType) {
            if (type != null) {
                return type.equals(otherType);
            }
            return (type == null && otherType == null);
        }
    }
}
