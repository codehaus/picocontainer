package org.picocontainer.gems.adapters;

public class Swappable {

    private transient Object delegate;

    public Object getInstance() {
        return delegate;
    }

    public Object swap(Object delegate) {
        Object old = this.delegate;
        this.delegate = delegate;
        return old;
    }
}