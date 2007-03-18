/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop.defaults;

import junit.framework.TestCase;
import org.nanocontainer.aop.ComponentPointcut;

/**
 * @author Stephen Molitor
 */
public class KeyEqualsComponentPointcutTestCase extends TestCase {

    public void testPicks() {
        ComponentPointcut pointcutA = new KeyEqualsComponentPointcut("a");
        ComponentPointcut pointcutB = new KeyEqualsComponentPointcut("b");

        assertTrue(pointcutA.picks("a"));
        assertFalse(pointcutA.picks("b"));
        assertFalse(pointcutB.picks("a"));
        assertTrue(pointcutB.picks("b"));
    }

    public void testConstructorChecksForNullComponentKey() {
        try {
            new KeyEqualsComponentPointcut(null);
            fail("NullPointerException should have been raised");
        } catch (NullPointerException e) {
        }
    }

}