dust
/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer;

import junit.framework.TestCase;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;

public class NoPicoTestCase extends TestCase {

    /**
     * RecordingAware2 demonstration of using components WITHOUT Pico (or Nano)
     * This was one of the design goals.
     *
     * This is manual lacing of components.
     */
    public void testTouchableWithoutPicoTestCase() {

        SimpleTouchable touchable = new SimpleTouchable();
        new DependsOnTouchable(touchable);

        assertTrue("Touchable should have had its wasTouched method called",
                touchable.wasTouched);
    }
}
