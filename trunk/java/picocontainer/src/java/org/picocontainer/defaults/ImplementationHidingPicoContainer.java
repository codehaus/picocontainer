/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * This special MutablePicoContainer hides implementations of components if the key is an interface.
 * It's very simple. Instances that are registered directly and components registered without key
 * are not hidden.
 *
 * @author Paul Hammant
 * @version $Revision$
 */
public class ImplementationHidingPicoContainer implements MutablePicoContainer, Serializable {

    private final DefaultPicoContainer pc;
    private final ComponentAdapterFactory caf;


    /**
     * Creates a new container with a parent container.
     */
    public ImplementationHidingPicoContainer(ComponentAdapterFactory caf, PicoContainer parent) {
        this.caf = caf;
        pc = new DefaultPicoContainer(caf, parent);
    }

    /**
     * Creates a new container with a parent container.
     */
    public ImplementationHidingPicoContainer(PicoContainer parent) {
        this(new DefaultComponentAdapterFactory(), parent);
    }


    /**
     * Creates a new container with no parent container.
     */
    public ImplementationHidingPicoContainer() {
        this(null);
    }


    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {

        if (componentKey instanceof Class) {
            Class clazz = (Class) componentKey;
            if (clazz.isInterface()) {
                ComponentAdapter delegate = caf.createComponentAdapter(componentKey, componentImplementation, new Parameter[0]);
                return pc.registerComponent(new CachingComponentAdapter(new ImplementationHidingComponentAdapter(delegate)));
            }
        }
        return pc.registerComponentImplementation(componentKey, componentImplementation);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException {
        if (componentKey instanceof Class) {
            Class clazz = (Class) componentKey;
            if (clazz.isInterface()) {
                ComponentAdapter delegate = caf.createComponentAdapter(componentKey, componentImplementation, new Parameter[0]);
                return pc.registerComponent(new CachingComponentAdapter(new ImplementationHidingComponentAdapter(delegate)));
            }
        }
        return pc.registerComponentImplementation(componentKey, componentImplementation, parameters);
    }

    public ComponentAdapter registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException {
        return pc.registerComponentImplementation(componentImplementation);
    }

    public ComponentAdapter registerComponentInstance(Object componentInstance) throws PicoRegistrationException {
        return pc.registerComponentInstance(componentInstance);
    }

    public ComponentAdapter registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException {
        return pc.registerComponentInstance(componentKey, componentInstance);
    }

    public ComponentAdapter registerComponent(ComponentAdapter componentAdapter) throws PicoRegistrationException {
        return pc.registerComponentInstance(componentAdapter);
    }

    public ComponentAdapter unregisterComponent(Object componentKey) {
        return pc.unregisterComponent(componentKey);
    }

    public ComponentAdapter unregisterComponentByInstance(Object componentInstance) {
        return pc.unregisterComponentByInstance(componentInstance);
    }

    public void setParent(PicoContainer parent) {
        throw new PicoIntrospectionException("setParent was deprecated by the time this class was created");
    }

    public Object getComponentInstance(Object componentKey) {
        return pc.getComponentInstance(componentKey);
    }

    public Object getComponentInstanceOfType(Class componentType) {
        return pc.getComponentInstanceOfType(componentType);
    }

    public List getComponentInstances() {
        return pc.getComponentInstances();
    }

    public PicoContainer getParent() {
        return pc.getParent();
    }

    public ComponentAdapter getComponentAdapter(Object componentKey) {
        return pc.getComponentAdapter(componentKey);
    }

    public ComponentAdapter getComponentAdapterOfType(Class componentType) {
        return pc.getComponentAdapterOfType(componentType);
    }

    public Collection getComponentAdapters() {
        return pc.getComponentAdapters();
    }

    public List getComponentAdaptersOfType(Class componentType) {
        return pc.getComponentAdaptersOfType(componentType);
    }

    public void verify() throws PicoVerificationException {
        pc.verify();
    }

    public void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        pc.addOrderedComponentAdapter(componentAdapter);
    }

    public void start() {
        pc.start();
    }

    public void stop() {
        pc.stop();
    }

    public void dispose() {
        pc.dispose();
    }
}
