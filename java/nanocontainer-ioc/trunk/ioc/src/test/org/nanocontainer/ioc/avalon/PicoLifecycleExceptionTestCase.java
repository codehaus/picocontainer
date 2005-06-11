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

import org.nanocontainer.ioc.avalon.PicoLifecycleException;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Id$
 */
public class PicoLifecycleExceptionTestCase extends TestCase {
    final String msg = "blah";
    
    public void testEverything()
    {
        final PicoLifecycleException e = new PicoLifecycleException(msg);
        assertEquals( msg, e.getMessage() );
    }
}
