/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.composite;

import picocontainer.PicoContainer;
import picocontainer.PicoInitializationException;
import picocontainer.defaults.DefaultComponentFactory;
import picocontainer.defaults.DefaultPicoContainer;
import picocontainer.defaults.PicoInvocationTargetInitializationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * CompositePicoContainer aggregates the the contents of more
 * than one container together for the sake of a single list of
 * components. This list may be used as the parent container for
 * another PicoContainer. This will result in directive graphs of
 * containers/components rather than just trees.
 *
 * It is not in itself, a Pico component (the array in the
 * constructor puts paid to that).
 *
 */
public class CompositePicoContainer extends DefaultPicoContainer {

    private final List containers = new ArrayList();

    public CompositePicoContainer(final PicoContainer[] containers) {
        super(new DefaultComponentFactory());
        if (containers == null) {
            throw new NullPointerException("containers can't be null");
        }
        for (int i = 0; i < containers.length; i++) {
            PicoContainer container = containers[i];
            if (container == null) {
                throw new NullPointerException("PicoContainer at position " + i + " was null");
            }
            this.containers.add(container);
        }
    }

    public static class Filter extends CompositePicoContainer {
        private final PicoContainer subject;

        public Filter(final PicoContainer container) {
            super(new PicoContainer[]{container});
            subject = container;
        }

        public PicoContainer getSubject() {
            return subject;
        }
    }

    public Object getComponent(Class componentType) {
        Object answer = super.getComponent(componentType);
        if (answer == null) {
            for (Iterator iter = containers.iterator(); iter.hasNext(); ) {
                PicoContainer container = (PicoContainer) iter.next();
                if (container.hasComponent(componentType)) {
                    return container.getComponent(componentType);
                }
            }
        }
        return answer;
    }

    public Class[] getComponentTypes() {
        Set componentTypes = new HashSet();
        componentTypes.addAll(Arrays.asList(super.getComponentTypes()));
        for (Iterator iter = containers.iterator(); iter.hasNext(); ) {
            PicoContainer container = (PicoContainer) iter.next();
            componentTypes.addAll(Arrays.asList(container.getComponentTypes()));
        }
        return (Class[]) componentTypes.toArray(new Class[containers.size()]);
    }

    public void instantiateComponents() throws PicoInvocationTargetInitializationException, PicoInitializationException {
        super.instantiateComponents();
        
        // @todo should we iterate through our child containers and instantiate those?
    }

    /**
     * Adds a new Pico container to this composite container
     * @param container
     */
    protected void addContainer(PicoContainer container) {
        containers.add(container);
    }
    
    /**
     * Removes a Pico container from this composite container
     * @param container
     */
    protected void removeContainer(PicoContainer container) {
        containers.remove(container);
    }
    
    /**
     * A helper method which adds each of the objects in the array to the list
     * 
     * @param list
     * @param objects
     */
    protected static void addAll(List list, Object[] objects) {
        for (int i = 0, size = objects.length; i < size; i++ ) {
            list.add(objects[i]);
        }
    }
}
