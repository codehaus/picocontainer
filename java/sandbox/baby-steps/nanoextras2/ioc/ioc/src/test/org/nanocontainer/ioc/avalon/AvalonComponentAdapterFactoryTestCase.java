/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.ioc.avalon;

import junit.framework.Test;
import junit.framework.TestCase;

import org.nanocontainer.ioc.avalon.AvalonComponentAdapter;
import org.nanocontainer.ioc.avalon.AvalonComponentAdapterFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Id$
 */
public class AvalonComponentAdapterFactoryTestCase extends TestCase {
    public void testEverything()
    {
        final AvalonComponentAdapterFactory f = new AvalonComponentAdapterFactory();
        final MutablePicoContainer pico = new DefaultPicoContainer(f);
        final ComponentAdapter adapter = pico.registerComponent(this, this.getClass(), new Parameter[0] );
        assertTrue( adapter instanceof AvalonComponentAdapter );
        assertEquals( this.getClass(), adapter.getComponentImplementation() );
        assertEquals( this, adapter.getComponentKey() );
        assertTrue( adapter.getComponentInstance(pico) instanceof Test );
    }
}
