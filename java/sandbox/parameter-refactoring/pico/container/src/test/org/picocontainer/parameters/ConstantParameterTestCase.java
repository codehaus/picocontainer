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
import org.picocontainer.PicoCompositionException;
import org.picocontainer.parameters.Constant;

/**
 * test that constant parameter behaves well.
 * @author Konstantin Pribluda
 */
public class ConstantParameterTestCase extends TestCase {
    
    /**
    *  constant parameter with instance type shall verify for expected primitives
     * @throws Exception
     */
    public void testThatInstaceTypeAcceptedForPrimitives() throws Exception {
        Constant param = new Constant(239);
        try{
            param.verify(null);
        } catch(PicoCompositionException ex) {
            fail("failed verification for primitive / instance ");
        }
    }
}
