/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.parameters;

import junit.framework.TestCase;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.parameters.ConstantParameter;

/**
 * test that constant parameter behaves well.
 * @author Konstantin Pribluda
 * @version $Revision$
 */
public class ConstantParameterTestCase extends TestCase {
    
    /**
    *  constant parameter with instance type shall verify for expected primitives
     * @throws Exception
     */
    public void testThatInstaceTypeAcceptedForPrimitives() throws Exception {
        ConstantParameter param = new ConstantParameter(239);
        try{
            param.verify(null,null,Integer.TYPE, null);
        } catch(PicoIntrospectionException ex) {
            fail("failed verification for primitive / instance ");
        }
    }
}
