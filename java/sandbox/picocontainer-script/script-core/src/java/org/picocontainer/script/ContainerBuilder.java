/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * ----------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.script;

import org.picocontainer.PicoContainer;

/**
 * The responsibility of a ContainerBuilder is to <em>instantiate</em> and
 * <em>compose</em> containers.
 * 
 * @author Joe Walnes
 * @author Mauro Talevi
 */
public interface ContainerBuilder {

    /**
     * Builds and composes a new container
     * 
     * @param parentContainer the parent PicoContainer (may be null).
     * @param compositionScope Hint about the scope for composition.
     * @param addChildToParent Add the child to the parent
     * @return A PicoContainer
     */
    PicoContainer buildContainer(PicoContainer parentContainer, Object compositionScope, boolean addChildToParent);

    /**
     * Stops, destroys and removed a container.
     * 
     * @param container the PicoContainer to be killed
     */
    void killContainer(PicoContainer container);

}
