/*****************************************************************************
 * Copyright (Cc) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.aggregated;

import picocontainer.defaults.DefaultPicoContainer;
import picocontainer.defaults.DefaultComponentFactory;
import picocontainer.PicoContainer;
import picocontainer.PicoInstantiationException;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

/**
 * AggregatedContainersContainer aggregates the the contents of more
 * than one container together for the sake of a single list of
 * components. This list may be used as the parent container for
 * another PicoContainer. This will result in directive graphs of
 * containers/components rather than just trees.
 *
 * It is not in itself, a Pico component (the array in the
 * constructor puts paid to that).
 *
 */
public class AggregatedContainersContainer extends DefaultPicoContainer {

    private final PicoContainer[] containers;

    public AggregatedContainersContainer(final PicoContainer[] containers) {
        super(new DefaultComponentFactory());
        if (containers == null) {
            throw new NullPointerException("containers can't be null");
        }
        for (int i = 0; i < containers.length; i++) {
            PicoContainer container = containers[i];
            if (container == null) {
                throw new NullPointerException("PicoContainer at position " + i + " was null");
            }
        }
        this.containers = containers;
    }

    public static class Filter extends AggregatedContainersContainer {
        private final PicoContainer subject;

        public Filter(final PicoContainer container) {
            super(new PicoContainer[]{container});
            subject = container;
        }

        public PicoContainer getSubject() {
            return subject;
        }
    }

    public boolean hasComponent(Class compType) {
        for (int i = 0; i < containers.length; i++) {
            PicoContainer container = containers[i];
            if (container.hasComponent(compType)) {
                return true;
            }
        }
        return false;
    }

    public Object getComponent(Class compType) {
        for (int i = 0; i < containers.length; i++) {
            PicoContainer container = containers[i];
            if (container.hasComponent(compType)) {
                return container.getComponent(compType);
            }
        }
        return null;
    }

    public Class[] getComponentTypes() {
        Set componentTypes = new HashSet();
        for (int i = 0; i < containers.length; i++) {
            PicoContainer container = containers[i];
            componentTypes.addAll(Arrays.asList(container.getComponentTypes()));
        }
        return (Class[]) componentTypes.toArray(new Class[containers.length]);
    }

    public void instantiateComponents() throws PicoInstantiationException {
    }
}
