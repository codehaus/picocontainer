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

import junit.framework.TestCase;
import org.nanocontainer.type1.PicoType1ContractException;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Id$
 */
public class PicoType1ContractExceptionTestCase extends TestCase {
    final Exception parent = new Exception("blah");
    final String msg = "blah";
    
    public void testEverything()
    {
        PicoType1ContractException e = new PicoType1ContractException(parent);
        assertEquals( parent, e.getCause() );

        e = new PicoType1ContractException(msg);
        assertEquals( msg, e.getMessage() );
    }
}
