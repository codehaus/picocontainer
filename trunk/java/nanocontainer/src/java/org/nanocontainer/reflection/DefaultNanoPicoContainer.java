/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer.reflection;

import org.nanocontainer.NanoPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.io.Serializable;

/**
 * This is a MutablePicoContainer that also supports soft composition. i.e. assembly by class name rather that class
 * reference.
 * <p/>
 * In terms of implementation it adopts the behaviour of DefaultPicoContainer and DefaulReflectionContainerAdapter
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 * @version $Revision$
 */
public class DefaultNanoPicoContainer extends AbstractNanoPicoContainer implements NanoPicoContainer, Serializable {

    public DefaultNanoPicoContainer(ClassLoader classLoader, ComponentAdapterFactory caf, PicoContainer parent) {
        super(new DefaultPicoContainer(caf, parent), classLoader);

    }

    public DefaultNanoPicoContainer(ClassLoader classLoader, PicoContainer parent) {
        super(new DefaultPicoContainer(new DefaultComponentAdapterFactory(), parent), classLoader);

    }

    public DefaultNanoPicoContainer(ComponentAdapterFactory caf) {
        super(new DefaultPicoContainer(caf, null), DefaultNanoPicoContainer.class.getClassLoader());
    }

    public DefaultNanoPicoContainer(PicoContainer pc) {
        super(new DefaultPicoContainer(pc), DefaultNanoPicoContainer.class.getClassLoader());
    }

    public DefaultNanoPicoContainer(ClassLoader classLoader) {
        super(new DefaultPicoContainer(), classLoader);
    }

    public DefaultNanoPicoContainer() {
        super(new DefaultPicoContainer(), DefaultNanoPicoContainer.class.getClassLoader());
    }

    public MutablePicoContainer makeChildContainer(String name) {
        ClassLoader currentClassloader = container.getComponentClassLoader();
        DefaultNanoPicoContainer child = new DefaultNanoPicoContainer(currentClassloader, this);
        getDelegate().addChildContainer(child);
        namedChildContainers.put(name, child);
        return child;
    }
    
}
