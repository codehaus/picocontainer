/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.nanocontainer.script.groovy.buildernodes;

import java.util.Map;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.picocontainer.defaults.DefaultPicoContainer;
import java.security.PrivilegedAction;
import org.picocontainer.defaults.ComponentAdapterFactory;
import java.security.AccessController;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.script.NodeBuilderDecorationDelegate;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.defaults.DelegatingComponentMonitor;
import org.picocontainer.defaults.ComponentMonitorStrategy;

/**
 * Creates a new NanoContainer node.  There may or may not be a parent
 * container involved.
 * @author James Strachan
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Michael Rimov
 * @author Mauro Talevi
 * @version $Revision: 2695 $
 */
public class ChildContainerNode extends AbstractBuilderNode {

    /**
     * Node name.
     */
    public static final String NODE_NAME = "container";

    /**
     * Supported Attribute: 'class'  Reference to a classname of the container
     * to use.
     */
    private static final String CLASS = "class";

    /**
     * The node decoration delegate.
     */
    private final NodeBuilderDecorationDelegate decorationDelegate;

    /**
     * Attribute: 'componentAdapterFactory' a reference to an instance of a
     * component adapter factory.
     */
    private static final String COMPONENT_ADAPTER_FACTORY = "componentAdapterFactory";

    /**
     * Attribute: 'componentMonitor' a reference to an instance of a component monitor.
     */
    private static final String COMPONENT_MONITOR = "componentMonitor";


    /**
     * Attribute that exists in test cases, but not necessarily used?
     *
     */
    private static final String SCOPE = "scope";


    /**
     * Attribute: 'parent'  a reference to the parent for this new container.
     */
    private static final String PARENT = "parent";


    /**
     * Constructs a child container node.  It requires a <tt>NodeBuilderDecorationDelegate</tt>
     * for construction.
     * @param delegate NodeBuilderDecorationDelegate
     */
    public ChildContainerNode(NodeBuilderDecorationDelegate delegate) {
        super(NODE_NAME);
        decorationDelegate = delegate;

        this.addAttribute(CLASS)
            .addAttribute(COMPONENT_ADAPTER_FACTORY)
            .addAttribute(COMPONENT_MONITOR)
            .addAttribute(PARENT)
            .addAttribute(SCOPE);


    }

    /**
     * Creates a new container.  There may or may not be a parent to this container.
     * Supported attributes are
     * <p>{@inheritDoc}</p>
     * @param current NanoContainer
     * @param attributes Map
     * @return Object
     * @throws NanoContainerMarkupException
     */
    public Object createNewNode(Object current, Map attributes) throws
        NanoContainerMarkupException {

        return createChildContainer(attributes,(NanoContainer) current);
    }

    /**
     * Retrieve the decoration delegate.
     * @return NodeBuilderDecorationDelegate
     */
    private NodeBuilderDecorationDelegate getDecorationDelegate() {
        return decorationDelegate;
    }



    /**
     * Creates a new container.  There may or may not be a parent to this container.
     * Supported attributes are:
     * <ul>
     *  <li><tt>componentAdapterFactory</tt>: The ComponentAdapterFactory used for new container</li>
     *  <li><tt>componentMonitor</tt>: The ComponentMonitor used for new container</li>
     * </ul>
     * @param attributes Map Attributes defined by the builder in the script.
     * @param parent The parent container
     * @return The NanoContainer
     */
    protected NanoContainer createChildContainer(Map attributes, NanoContainer parent) {

        ClassLoader parentClassLoader = null;
        MutablePicoContainer childContainer = null;
        if (parent != null) {
            parentClassLoader = parent.getComponentClassLoader();
            if ( isAttribute(attributes, COMPONENT_ADAPTER_FACTORY) ) {
                ComponentAdapterFactory componentAdapterFactory = createComponentAdapterFactory(attributes);
                childContainer = new DefaultPicoContainer(
                        getDecorationDelegate().decorate(componentAdapterFactory, attributes), parent.getPico());
                if ( isAttribute(attributes, COMPONENT_MONITOR) ) {
                    changeComponentMonitor(childContainer, createComponentMonitor(attributes));
                }
                parent.getPico().addChildContainer(childContainer);
            } else if ( isAttribute(attributes, COMPONENT_MONITOR) ) {
                ComponentAdapterFactory componentAdapterFactory = new DefaultComponentAdapterFactory(
                                                    createComponentMonitor(attributes));
                childContainer = new DefaultPicoContainer(
                        getDecorationDelegate().decorate(componentAdapterFactory, attributes), parent.getPico());
            } else {
                childContainer = parent.getPico().makeChildContainer();
            }
        } else {
            parentClassLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return PicoContainer.class.getClassLoader();
                }
            });
            ComponentAdapterFactory componentAdapterFactory = createComponentAdapterFactory(attributes);
            childContainer = new DefaultPicoContainer(
                    getDecorationDelegate().decorate(componentAdapterFactory, attributes));
            if ( isAttribute(attributes, COMPONENT_MONITOR) ) {
                changeComponentMonitor(childContainer, createComponentMonitor(attributes));
            }
        }

        MutablePicoContainer decoratedPico = getDecorationDelegate().decorate(childContainer);
        if ( isAttribute(attributes, CLASS) )  {
            Class clazz = (Class) attributes.get(CLASS);
            return createNanoContainer(clazz, decoratedPico, parentClassLoader);
        } else {
            return new DefaultNanoContainer(parentClassLoader, decoratedPico);
        }
    }

    private void changeComponentMonitor(MutablePicoContainer childContainer, ComponentMonitor monitor) {
        if ( childContainer instanceof ComponentMonitorStrategy ){
            ((ComponentMonitorStrategy)childContainer).changeMonitor(monitor);
        }
    }

    private NanoContainer createNanoContainer(Class clazz, MutablePicoContainer decoratedPico, ClassLoader parentClassLoader) {
        DefaultPicoContainer instantiatingContainer = new DefaultPicoContainer();
        instantiatingContainer.registerComponentInstance(ClassLoader.class, parentClassLoader);
        instantiatingContainer.registerComponentInstance(MutablePicoContainer.class, decoratedPico);
        instantiatingContainer.registerComponentImplementation(NanoContainer.class, clazz);
        Object componentInstance = instantiatingContainer.getComponentInstance(NanoContainer.class);
        return (NanoContainer) componentInstance;
    }

    private ComponentAdapterFactory createComponentAdapterFactory(Map attributes) {
        final ComponentAdapterFactory factory = (ComponentAdapterFactory) attributes.remove(COMPONENT_ADAPTER_FACTORY);
        if ( factory == null ){
            return new DefaultComponentAdapterFactory();
        }
        return factory;
    }

    private ComponentMonitor createComponentMonitor(Map attributes) {
        final ComponentMonitor monitor = (ComponentMonitor) attributes.remove(COMPONENT_MONITOR);
        if ( monitor == null ){
            return new DelegatingComponentMonitor();
        }
        return monitor;
    }


}
