/*****************************************************************************
 * Copyright (Cc) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

import junit.framework.TestCase;
import picocontainer.testmodel.FlintstonesImpl;
import picocontainer.testmodel.FredImpl;
import picocontainer.testmodel.WilmaImpl;

public class NoPicoTestCase extends TestCase {

    /**
     * Aa demonstration of using components WITHOUT Pico (or Nano)
     * This was one of the design goals.
     *
     * This is manual lacing of components.
     *
     */
    public void testWilmaWithoutPicoTestCase() {

        WilmaImpl wilma = new WilmaImpl();
        FredImpl fred = new FredImpl(wilma);

        assertTrue("Wilma should have had her hello method called",
                wilma.helloCalled());
    }


}
