/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.defaults;

import org.nanocontainer.nanoaop.ComponentPointcut;

import junit.framework.TestCase;

/**
 * @author Stephen Molitor
 */
public class DefaultComponentPointcutTestCase extends TestCase {
    
    public void testGetComponentKey() {
        ComponentPointcut pointcutA = new DefaultComponentPointcut("a");
        assertEquals("a", pointcutA.getComponentKey());
        
        ComponentPointcut pointcutB = new DefaultComponentPointcut("b");
        assertEquals("b", pointcutB.getComponentKey());
    }
    
    public void testPicks() {
        ComponentPointcut pointcutA = new DefaultComponentPointcut("a");
        ComponentPointcut pointcutB = new DefaultComponentPointcut("b");
        
        assertTrue(pointcutA.picks("a"));
        assertFalse(pointcutA.picks("b"));
        assertFalse(pointcutB.picks("a"));
        assertTrue(pointcutB.picks("b"));
    }
    
    public void testConstructorChecksForNullComponentKey() {
        try {
            new DefaultComponentPointcut(null);
            fail("NullPointerException should have been raised");
        } catch (NullPointerException e) {
        }
    }

}
