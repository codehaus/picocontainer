/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.aop.pointcuts;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.PatternSyntaxException;

import org.junit.Test;
import org.picocontainer.aop.ComponentPointcut;
import org.picocontainer.aop.pointcuts.NameMatchesComponentPointcut;
import org.picocontainer.script.testmodel.Dao;

/**
 * @author Stephen Molitor
 */
public class NameMatchesComponentPointcutTest {

    @Test
    public void testPicks() throws Exception {
        ComponentPointcut cut1 = new NameMatchesComponentPointcut("^foo$");
        assertTrue(cut1.picks("foo"));
        assertFalse(cut1.picks("barfoo"));
        assertFalse(cut1.picks("foobar"));

        ComponentPointcut cut2 = new NameMatchesComponentPointcut(".*foo.*");
        assertTrue(cut2.picks("foo"));
        assertTrue(cut2.picks("barfoo"));
        assertTrue(cut2.picks("foobar"));
    }

    @Test
    public void testNotStringComponentKey() {
        ComponentPointcut cut = new NameMatchesComponentPointcut("foo");
        assertFalse(cut.picks(Dao.class));
    }

    @Test(expected = PatternSyntaxException.class)
    public void testConstructorThrowsMalformedRegularExpressionException() {
        new NameMatchesComponentPointcut("(");
    }

}
