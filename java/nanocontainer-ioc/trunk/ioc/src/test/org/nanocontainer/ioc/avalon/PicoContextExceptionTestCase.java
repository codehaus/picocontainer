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

import junit.framework.TestCase;
import org.apache.avalon.framework.context.ContextException;

import org.nanocontainer.ioc.avalon.PicoContextException;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Id$
 */
public class PicoContextExceptionTestCase extends TestCase {
    final ContextException parent = new ContextException("blah");
    
    public void testEverything()
    {
        final PicoContextException e = new PicoContextException(parent);
        assertEquals( parent, e.getCause() );
    }
}
