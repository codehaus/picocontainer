/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.type1.test;

import junit.framework.Test;
import junit.framework.TestCase;
import org.nanocontainer.type1.Type1ComponentAdapter;
import org.nanocontainer.type1.Type1ComponentAdapterFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Id$
 */
public class Type1ComponentAdapterFactoryTestCase extends TestCase {
    public void testEverything()
    {
        final Type1ComponentAdapterFactory f = new Type1ComponentAdapterFactory();
        final MutablePicoContainer pico = new DefaultPicoContainer(f);
        final ComponentAdapter adapter = pico.registerComponentImplementation(this, this.getClass(), new Parameter[0] );
        assertTrue( adapter instanceof Type1ComponentAdapter );
        assertEquals( this.getClass(), adapter.getComponentImplementation() );
        assertEquals( this, adapter.getComponentKey() );
        assertTrue( adapter.getComponentInstance(pico) instanceof Test );
    }
}
