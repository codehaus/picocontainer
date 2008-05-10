/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/
package org.picocontainer.containers;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.NameBinding;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.lang.annotation.Annotation;

/**
 * empty pico container serving as recoil damper in situations where you
 * do not like to check whether container reference suplpied to you
 * is null or not
 *
 * @author Konstantin Pribluda
 */
public class EmptyPicoContainer implements PicoContainer, Serializable {
    private static final long serialVersionUID = -2225539108410682231L;

    public Object getComponent(Object componentKeyOrType) {
        return null;
    }

    public <T> T getComponent(Class<T> componentType) {
        return null;
    }

    public <T> T getComponent(Class<T> componentType, Class<? extends Annotation> binding) {
        return null;
    }

    public List getComponents() {
        return Collections.EMPTY_LIST;
    }

    public PicoContainer getParent() {
        return null;
    }

    public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
        return null;
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, NameBinding componentNameBinding) {
        return null;
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, Class<? extends Annotation> binding) {
        return null;
    }

    public Collection<ComponentAdapter<?>> getComponentAdapters() {
        return Collections.emptyList();
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
        return Collections.emptyList();
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType, Class<? extends Annotation> binding) {
        return Collections.emptyList();
    }

    /**
     * we do not have anything to do here. 
     */
    public void accept(PicoVisitor visitor) {
    }

    public <T> List<T> getComponents(Class<T> componentType) {
        return Collections.emptyList();
    }

}
