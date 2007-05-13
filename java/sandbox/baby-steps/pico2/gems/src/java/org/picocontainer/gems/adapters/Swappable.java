package org.picocontainer.gems.adapters;

public class Swappable {

    private transient Object delegate;

    public Object getDelegate() {
        return delegate;
    }

    public void setDelegate(Object delegate) {
        this.delegate = delegate;
    }
}