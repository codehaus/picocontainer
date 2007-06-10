/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.adapters.InjectingAdapter;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;

public class DelegatingPicoContainerTestCase extends TestCase {
    private MutablePicoContainer parent;
    private DefaultPicoContainer child;

    public void setUp() throws PicoRegistrationException, PicoInitializationException {
        parent = new DefaultPicoContainer();
        child = new DefaultPicoContainer(parent);
    }

    public void testChildGetsFromParent() {
        parent.addComponent(SimpleTouchable.class);
        child.addComponent(DependsOnTouchable.class);
        DependsOnTouchable dependsOnTouchable = child.getComponent(DependsOnTouchable.class);

        assertNotNull(dependsOnTouchable);
    }

    public void testParentDoesntGetFromChild() {
        child.addComponent(SimpleTouchable.class);
        parent.addComponent(DependsOnTouchable.class);
        try {
            parent.getComponent(DependsOnTouchable.class);
            fail();
        } catch (InjectingAdapter.UnsatisfiableDependenciesException e) {
        }
    }

    public void testChildOverridesParent() {
        parent.addComponent(SimpleTouchable.class);
        child.addComponent(SimpleTouchable.class);

        SimpleTouchable parentTouchable = parent.getComponent(SimpleTouchable.class);
        SimpleTouchable childTouchable = child.getComponent(SimpleTouchable.class);
        assertEquals(1, child.getComponents().size());
        assertNotSame(parentTouchable, childTouchable);
    }
}
