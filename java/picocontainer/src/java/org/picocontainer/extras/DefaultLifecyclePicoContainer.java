package org.picocontainer.extras;

import org.picocontainer.lifecycle.LifecyclePicoAdapter;
import org.picocontainer.*;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.Collection;
import java.util.List;

public class DefaultLifecyclePicoContainer implements LifecyclePicoAdapter, MutablePicoContainer {

    private final MutablePicoContainer mutablePicoContainer;
    private final LifecyclePicoAdapter lifecyclePicoAdapter;

    public DefaultLifecyclePicoContainer(LifecyclePicoAdapter lifecyclePicoAdapter, MutablePicoContainer mutablePicoContainer) {
        this.lifecyclePicoAdapter = lifecyclePicoAdapter;
        this.mutablePicoContainer = mutablePicoContainer;
    }

    public DefaultLifecyclePicoContainer(MutablePicoContainer mutablePicoContainer) {
        this.mutablePicoContainer = mutablePicoContainer;
        lifecyclePicoAdapter = new DefaultLifecyclePicoAdapter(mutablePicoContainer);
    }

    public DefaultLifecyclePicoContainer() {
        mutablePicoContainer = new DefaultPicoContainer();
        lifecyclePicoAdapter = new DefaultLifecyclePicoAdapter(mutablePicoContainer);
    }

    public ComponentAdapter findComponentAdapter(Object componentKey) throws PicoIntrospectionException {
        return mutablePicoContainer.findComponentAdapter(componentKey);
    }

    public void dispose() {
        lifecyclePicoAdapter.dispose();
    }

    public void start() {
        lifecyclePicoAdapter.start();
    }

    public PicoContainer getPicoContainer() {
        return mutablePicoContainer; // ?
    }

    public boolean isStarted() {
        return lifecyclePicoAdapter.isStarted();
    }

    public Object getComponentInstance(Object componentKey) throws PicoException {
        return mutablePicoContainer.getComponentInstance(componentKey);
    }

    public Collection getChildContainers() {
        return mutablePicoContainer.getChildContainers();
    }

    public void stop() {
        lifecyclePicoAdapter.stop();
    }

    public boolean isDisposed() {
        return lifecyclePicoAdapter.isDisposed();
    }

    public Object getComponentMulticaster() throws PicoException {
        return mutablePicoContainer.getComponentMulticaster();
    }

    public Collection getComponentKeys() {
        return mutablePicoContainer.getComponentKeys();
    }

    public boolean isStopped() {
        return lifecyclePicoAdapter.isStopped();
    }

    public boolean hasComponent(Object componentKey) {
        return mutablePicoContainer.hasComponent(componentKey);
    }

    public List getComponentInstances() throws PicoException {
        return mutablePicoContainer.getComponentInstances();
    }

    public Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) throws PicoException {
        return mutablePicoContainer.getComponentMulticaster(callInInstantiationOrder, callUnmanagedComponents);
    }

    public List getParentContainers() {
        return mutablePicoContainer.getParentContainers();
    }

    public Object unregisterComponent(Object componentKey) {
        return mutablePicoContainer.unregisterComponent(componentKey);
    }

    public Object registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException {
        return mutablePicoContainer.registerComponentInstance(componentKey, componentInstance);
    }

    public void addChild(MutablePicoContainer child) {
        mutablePicoContainer.addChild(child);
    }

    public void addParent(MutablePicoContainer parent) {
        mutablePicoContainer.addParent(parent);
    }

    public Object registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {
        return mutablePicoContainer.registerComponentImplementation(componentKey, componentImplementation);
    }

    public Object registerComponentInstance(Object componentInstance) throws PicoRegistrationException {
        return mutablePicoContainer.registerComponentInstance(componentInstance);
    }

    public void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        mutablePicoContainer.addOrderedComponentAdapter(componentAdapter);
    }

    public Object registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException {
        return mutablePicoContainer.registerComponentImplementation(componentImplementation);
    }

    public Object registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException {
        return mutablePicoContainer.registerComponentImplementation(componentKey, componentImplementation);
    }
}
