/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer;

import java.io.Serializable;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ComponentMonitorStrategy;
import org.picocontainer.adapters.AnyInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.LifecycleStrategy;

/**
 * This is a MutablePicoContainer that also supports soft composition. i.e. assembly by class name rather that class
 * reference.
 * <p>
 * In terms of implementation it adopts the behaviour of DefaultPicoContainer and DefaultNanoContainer.</p>
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Michael Rimov
 * @version $Revision$
 */
public class DefaultNanoContainer extends AbstractNanoContainer implements NanoContainer, Serializable,
    ComponentMonitorStrategy {

    public DefaultNanoContainer(ClassLoader classLoader, ComponentAdapterFactory caf, PicoContainer parent) {
        super(new DefaultPicoContainer(caf, parent), classLoader);
    }

    public DefaultNanoContainer(ClassLoader classLoader, PicoContainer parent) {
        super(new DefaultPicoContainer(new CachingComponentAdapterFactory(new AnyInjectionComponentAdapterFactory()), parent), classLoader);
    }

    public DefaultNanoContainer(ClassLoader classLoader, MutablePicoContainer delegate) {
        super(delegate, classLoader);
    }

    public DefaultNanoContainer(ClassLoader classLoader, PicoContainer parent, ComponentMonitor componentMonitor) {
        super(new DefaultPicoContainer(new CachingComponentAdapterFactory(new AnyInjectionComponentAdapterFactory()), parent), classLoader);
        ((ComponentMonitorStrategy)getDelegate()).changeMonitor(componentMonitor);
    }

    public DefaultNanoContainer(ComponentAdapterFactory caf) {
        super(new DefaultPicoContainer(caf, null), DefaultNanoContainer.class.getClassLoader());
    }

    public DefaultNanoContainer(PicoContainer parent) {
        super(new DefaultPicoContainer(parent), DefaultNanoContainer.class.getClassLoader());
    }

    public DefaultNanoContainer(MutablePicoContainer pico) {
        super(pico, DefaultNanoContainer.class.getClassLoader());
    }

    public DefaultNanoContainer(ClassLoader classLoader) {
        super(new DefaultPicoContainer(), classLoader);
    }

    public DefaultNanoContainer() {
        super(new DefaultPicoContainer(), DefaultNanoContainer.class.getClassLoader());
    }


    /**
     * Constructor that provides the same control over the nanocontainer lifecycle strategies
     * as {@link DefaultPicoContainer(ComponentAdapterFactory, LifecycleStrategy, PicoContainer)}.
     * @param componentAdapterFactory ComponentAdapterFactory
     * @param lifecycleStrategy LifecycleStrategy
     * @param parent PicoContainer may be null if there is no parent.
     * @param cl the Classloader to use.  May be null, in which case DefaultNanoPicoContainer.class.getClassLoader()
     * will be called instead.
     */
    public DefaultNanoContainer(ComponentAdapterFactory componentAdapterFactory,
        LifecycleStrategy lifecycleStrategy, PicoContainer parent, ClassLoader cl) {

        super(new DefaultPicoContainer(componentAdapterFactory,
            lifecycleStrategy, parent),
            //Use a default classloader if none is specified.
            (cl != null) ? cl : DefaultNanoContainer.class.getClassLoader());
    }


    protected AbstractNanoContainer createChildContainer() {
        MutablePicoContainer child = getDelegate().makeChildContainer();
        DefaultNanoContainer container = new DefaultNanoContainer(getComponentClassLoader(), child);
        container.changeMonitor(currentMonitor());
        return container;
     }

    public void changeMonitor(ComponentMonitor monitor) {
        ((ComponentMonitorStrategy)getDelegate()).changeMonitor(monitor);
    }

    public ComponentMonitor currentMonitor() {
        return ((ComponentMonitorStrategy)getDelegate()).currentMonitor();
    }

}