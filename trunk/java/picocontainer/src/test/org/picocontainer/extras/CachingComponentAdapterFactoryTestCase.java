/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.defaults.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ConstructorComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id$
 */
public class CachingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase
{
    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer(createComponentAdapterFactory());
    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
         return new CachingComponentAdapterFactory(new ConstructorComponentAdapterFactory());
    }

    public void testContainerReturnsSameInstaceEachCall()
    {
        picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        Touchable t1 = (Touchable)picoContainer.getComponentInstance(Touchable.class);
        Touchable t2 = (Touchable)picoContainer.getComponentInstance(Touchable.class);
        assertSame(t1, t2);
    }
}
