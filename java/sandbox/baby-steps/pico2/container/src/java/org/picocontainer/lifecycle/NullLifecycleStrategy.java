package org.picocontainer.lifecycle;

import org.picocontainer.defaults.LifecycleStrategy;

public class NullLifecycleStrategy implements LifecycleStrategy {

    public void start(Object component) {
    }

    public void stop(Object component) {
    }

    public void dispose(Object component) {
    }

    public boolean hasLifecycle(Class type) {
        return false;
    }
}
