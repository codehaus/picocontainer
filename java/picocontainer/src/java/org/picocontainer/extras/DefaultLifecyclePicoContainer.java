package org.picocontainer.extras;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.lifecycle.LifecyclePicoAdapter;

import java.util.Collection;
import java.util.List;

/**
 * @deprecated Use {@link DefaultLifecyclePicoAdapter} instead.
 */
public class DefaultLifecyclePicoContainer implements LifecyclePicoAdapter, MutablePicoContainer {

    private final MutablePicoContainer mutablePicoContainer;
    private final LifecyclePicoAdapter lifecyclePicoAdapter;

    public DefaultLifecyclePicoContainer(LifecyclePicoAdapter lifecyclePicoAdapter, MutablePicoContainer mutablePicoContainer) {
        this.lifecyclePicoAdapter = lifecyclePicoAdapter;
        this.mutablePicoContainer = mutablePicoContainer;
    }

    public DefaultLifecyclePicoContainer(DefaultPicoContainer mutablePicoContainer) {
        this(new DefaultLifecyclePicoAdapter(mutablePicoContainer), mutablePicoContainer);
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

    public List getUnmanagedComponentInstances() throws PicoException {
        return mutablePicoContainer.getUnmanagedComponentInstances();
    }

    public List getParentContainers() {
        return mutablePicoContainer.getParentContainers();
    }

    public ComponentAdapter unregisterComponent(Object componentKey) {
        return mutablePicoContainer.unregisterComponent(componentKey);
    }

    public ComponentAdapter registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException {
        return mutablePicoContainer.registerComponentInstance(componentKey, componentInstance);
    }

    public void registerComponent(ComponentAdapter componentAdapter) throws PicoRegistrationException {
        mutablePicoContainer.registerComponent(componentAdapter);
    }

    public boolean addChild(MutablePicoContainer child) {
        return mutablePicoContainer.addChild(child);
    }

    public boolean addParent(MutablePicoContainer parent) {
        return mutablePicoContainer.addParent(parent);
    }

    public boolean removeChild(MutablePicoContainer child) {
        return mutablePicoContainer.removeChild(child);
    }

    public boolean removeParent(MutablePicoContainer parent) {
        return mutablePicoContainer.removeParent(parent);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {
        return mutablePicoContainer.registerComponentImplementation(componentKey, componentImplementation);
    }

    public ComponentAdapter registerComponentInstance(Object componentInstance) throws PicoRegistrationException {
        return mutablePicoContainer.registerComponentInstance(componentInstance);
    }

    public void registerOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        mutablePicoContainer.registerOrderedComponentAdapter(componentAdapter);
    }

    public void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        mutablePicoContainer.addOrderedComponentAdapter(componentAdapter);
    }

    public ComponentAdapter registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException {
        return mutablePicoContainer.registerComponentImplementation(componentImplementation);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException {
        return mutablePicoContainer.registerComponentImplementation(componentKey, componentImplementation);
    }

    public void verify() {
        mutablePicoContainer.verify();
    }
}
