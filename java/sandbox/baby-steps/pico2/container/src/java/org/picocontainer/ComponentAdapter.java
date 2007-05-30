/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer;

/**
 * A addComponent addAdapter is responsible for providing a specific addComponent instance. An instance of an implementation of
 * this interface is used inside a {@link PicoContainer} for every registered addComponent or instance.  Each
 * <code>ComponentAdapter</code> instance has to have a key which is unique within that container. The key itself is
 * either a class type (normally an interface) or an identifier.
 *
 * @author Jon Tirs&eacute;n
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 * @see MutablePicoContainer an extension of the PicoContainer interface which allows you to modify the contents of the
 *      container.
 * @since 1.0
 */
public interface ComponentAdapter {
    /**
     * Retrieve the key associated with the addComponent.
     * 
     * @return the addComponent's key. Should either be a class type (normally an interface) or an identifier that is
     *         unique (within the scope of the current PicoContainer).
     */
    Object getComponentKey();    

    /**
     * Retrieve the class of the addComponent.
     * 
     * @return the addComponent's implementation class. Should normally be a concrete class (ie, a class that can be
     *         instantiated).
     */
    Class getComponentImplementation();

    /**
     * Retrieve the addComponent instance. This method will usually create a new instance each time it is called, but that
     * is not required. For example, {@link org.picocontainer.adapters.CachingBehaviorAdapter} will always return the
     * same instance.
     * 
     * @param container the {@link PicoContainer}, that is used to resolve any possible dependencies of the instance.
     * @return the addComponent instance.
     * @throws PicoInitializationException if the addComponent could not be instantiated.
     * @throws PicoIntrospectionException  if the addComponent has dependencies which could not be resolved, or
     *                                     instantiation of the addComponent lead to an ambigous situation within the
     *                                     container.
     */
    Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException;

    /**
     * Verify that all dependencies for this addAdapter can be satisifed. Normally, the addAdapter should verify this by
     * checking that the associated PicoContainer contains all the needed dependnecies.
     *
     * @param container the {@link PicoContainer}, that is used to resolve any possible dependencies of the instance.
     * @throws PicoIntrospectionException if one or more dependencies cannot be resolved.
     */
    void verify(PicoContainer container) throws PicoIntrospectionException;

    /**
     * Accepts a visitor for this ComponentAdapter. The method is normally called by visiting a {@link PicoContainer}, that 
     * cascades the visitor also down to all its ComponentAdapter instances.
     * 
     * @param visitor the visitor.
     * @since 1.1
     */
    void accept(PicoVisitor visitor);
}
