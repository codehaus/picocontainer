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

import org.nanocontainer.ioc.avalon.PicoAvalonContractException;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Id$
 */
public class PicoAvalonContractExceptionTestCase extends TestCase {
    final Exception parent = new Exception("blah");
    final String msg = "blah";
    
    public void testEverything()
    {
        PicoAvalonContractException e = new PicoAvalonContractException(parent);
        assertEquals( parent, e.getCause() );

        e = new PicoAvalonContractException(msg);
        assertEquals( msg, e.getMessage() );
    }
}
