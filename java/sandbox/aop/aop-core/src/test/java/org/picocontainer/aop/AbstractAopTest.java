/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 *****************************************************************************/
package org.picocontainer.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.picocontainer.script.testmodel.Dao;
import org.picocontainer.script.testmodel.Identifiable;

/**
 * @author Stephen Molitor
 */
public abstract class AbstractAopTest {

    protected void verifyIntercepted(Dao dao, StringBuffer log) {
        String before = log.toString();
        String data = dao.loadData();
        assertEquals("data", data);
        assertEquals(before + "startend", log.toString());
    }

    protected void verifyNotIntercepted(Dao dao, StringBuffer log) {
        String before = log.toString();
        String data = dao.loadData();
        assertEquals("data", data);
        assertEquals(before, log.toString());
    }

    protected void verifyMixin(Object component) {
        assertTrue(component instanceof Identifiable);
        Identifiable identifiable = (Identifiable) component;
        identifiable.setId("id");
        assertEquals("id", identifiable.getId());
    }

    protected void verifyNoMixin(Object component) {
        assertFalse(component instanceof Identifiable);
    }

}
