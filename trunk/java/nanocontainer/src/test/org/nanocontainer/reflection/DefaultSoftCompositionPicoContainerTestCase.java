/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer.reflection;

import org.nanocontainer.SoftCompositionPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.tck.AbstractPicoContainerTestCase;

import java.util.List;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class DefaultSoftCompositionPicoContainerTestCase extends AbstractPicoContainerTestCase {

    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new DefaultSoftCompositionPicoContainer(this.getClass().getClassLoader(), parent);
    }

    // TODO - go to a Nano TCK?
    public void testNamedChildContainerIsAccessible() {
        StringBuffer sb = new StringBuffer();
        final SoftCompositionPicoContainer parent = (SoftCompositionPicoContainer) createPicoContainer(null);
        parent.registerComponentInstance(sb);
        final SoftCompositionPicoContainer child = (SoftCompositionPicoContainer) parent.makeChildContainer("foo");
        child.registerComponentImplementation(LifeCycleMonitoring.class,LifeCycleMonitoring.class);
        Object o = parent.getComponentInstance("foo/*" + LifeCycleMonitoring.class.getName());
        assertNotNull(o);
        List keys = parent.getComponentKeys();
        assertEquals(2, keys.size());
        assertEquals("foo/*" + LifeCycleMonitoring.class.getName(), keys.get(1));
    }

    // TODO - go to a Nano TCK?
    public void testNamedChildContainerIsAccessibleForStringKeys() {
        StringBuffer sb = new StringBuffer();
        final MutablePicoContainer parent = createPicoContainer(null);
        parent.registerComponentInstance(sb);
        final MutablePicoContainer child = parent.makeChildContainer("foo");
        child.registerComponentImplementation("lcm",LifeCycleMonitoring.class);
        Object o = parent.getComponentInstance("foo/lcm");
        assertNotNull(o);
        assertTrue(sb.toString().indexOf("-instantiated") != -1);
    }

    // TODO - go to a Nano TCK?
    public void testNamedChildContainerIsAccessibleForClassKeys() {
        StringBuffer sb = new StringBuffer();
        final MutablePicoContainer parent = createPicoContainer(null);
        parent.registerComponentInstance(sb);
        final MutablePicoContainer child = parent.makeChildContainer("foo");
        child.registerComponentImplementation(LifeCycleMonitoring.class,LifeCycleMonitoring.class);
        Object o = parent.getComponentInstance("foo/*" + LifeCycleMonitoring.class.getName());
        assertNotNull(o);
        assertTrue(sb.toString().indexOf("-instantiated") != -1);
    }

    // test methods inherited. This container is otherwise fully compliant.
}
