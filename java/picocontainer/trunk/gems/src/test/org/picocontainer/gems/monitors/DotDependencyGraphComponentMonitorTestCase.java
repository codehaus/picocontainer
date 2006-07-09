/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/
package org.picocontainer.gems.monitors;

import junit.framework.TestCase;
import org.picocontainer.testmodel.DependsOnList;
import org.picocontainer.testmodel.DependsOnDependsOnListAndVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class DotDependencyGraphComponentMonitorTestCase extends TestCase {

    DotDependencyGraphComponentMonitor monitor;

    protected void setUp() throws Exception {
        monitor = new DotDependencyGraphComponentMonitor();

        Vector vec = new Vector();
        List list = new ArrayList();
        DependsOnList dol = new DependsOnList(list);
        DependsOnDependsOnListAndVector dodolav = new DependsOnDependsOnListAndVector(vec, dol);

        monitor.instantiated(Vector.class.getConstructor(new Class[0]), vec, new Object[]{}, 8);
        monitor.instantiated(ArrayList.class.getConstructor(new Class[]{Collection.class}), list, new Object[]{vec}, 12);
        monitor.instantiated(DependsOnList.class.getConstructors()[0], dol, new Object[]{list}, 10);
        monitor.instantiated(DependsOnDependsOnListAndVector.class.getConstructors()[0], dodolav, new Object[]{vec, dol}, 16);
        monitor.instantiated(DependsOnDependsOnListAndVector.class.getConstructors()[0], dodolav, new Object[]{vec, dol}, 12);
        monitor.instantiated(DependsOnDependsOnListAndVector.class.getConstructors()[0], dodolav, new Object[]{vec, dol}, 9);
    }

    public void testSimpleClassDependencyGraphCanEliminateDupes() throws NoSuchMethodException {
        String expected = "" +
                "  java.util.ArrayList -> java.util.Vector;\n" +
                "  org.picocontainer.testmodel.DependsOnDependsOnListAndVector -> java.util.Vector;\n" +
                "  org.picocontainer.testmodel.DependsOnDependsOnListAndVector -> org.picocontainer.testmodel.DependsOnList;\n" +
                "  org.picocontainer.testmodel.DependsOnList -> java.util.ArrayList;\n" +
                "";
        assertEquals(expected, monitor.getClassDependencyGraph());
    }



}
