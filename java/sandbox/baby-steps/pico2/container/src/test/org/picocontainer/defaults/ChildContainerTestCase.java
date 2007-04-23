/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.testmodel.AlternativeTouchable;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Rafal Krzewski
 * @version $Revision$
 */
public class ChildContainerTestCase extends TestCase {

    public void testParentContainerWithComponentWithEqualKeyShouldBeShadowedByChild() throws Exception {
        DefaultPicoContainer parent = new DefaultPicoContainer();
        DefaultPicoContainer child = new DefaultPicoContainer(parent);

        parent.registerComponent("key", AlternativeTouchable.class);
        child.registerComponent("key", SimpleTouchable.class);
        child.registerComponent(DependsOnTouchable.class);

        DependsOnTouchable dot = (DependsOnTouchable) child.getComponent(DependsOnTouchable.class);
        assertEquals(SimpleTouchable.class, dot.getTouchable().getClass());
    }

    public void testParentComponentRegisteredAsClassShouldBePreffered() throws Exception {
        DefaultPicoContainer parent = new DefaultPicoContainer();
        DefaultPicoContainer child = new DefaultPicoContainer(parent);

        parent.registerComponent(Touchable.class, AlternativeTouchable.class);
        child.registerComponent("key", SimpleTouchable.class);
        child.registerComponent(DependsOnTouchable.class);

        DependsOnTouchable dot = (DependsOnTouchable) child.getComponent(DependsOnTouchable.class);
        assertEquals(AlternativeTouchable.class, dot.getTouchable().getClass());
    }

    public void testResolveFromParentByType() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.registerComponent(Touchable.class, SimpleTouchable.class);

        MutablePicoContainer child = new DefaultPicoContainer(parent);
        child.registerComponent(DependsOnTouchable.class);

        assertNotNull(child.getComponent(DependsOnTouchable.class));
    }

    public void testResolveFromParentByKey() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.registerComponent(Touchable.class, SimpleTouchable.class);

        MutablePicoContainer child = new DefaultPicoContainer(parent);
        child.registerComponent(DependsOnTouchable.class, DependsOnTouchable.class,
                new Parameter[]{new ComponentParameter(Touchable.class)});

        assertNotNull(child.getComponent(DependsOnTouchable.class));
    }

    public void testResolveFromGrandParentByType() {
        MutablePicoContainer grandParent = new DefaultPicoContainer();
        grandParent.registerComponent(Touchable.class, SimpleTouchable.class);

        MutablePicoContainer parent = new DefaultPicoContainer(grandParent);

        MutablePicoContainer child = new DefaultPicoContainer(parent);
        child.registerComponent(DependsOnTouchable.class);

        assertNotNull(child.getComponent(DependsOnTouchable.class));
    }

    public void testResolveFromGrandParentByKey() {
        MutablePicoContainer grandParent = new DefaultPicoContainer();
        grandParent.registerComponent(Touchable.class, SimpleTouchable.class);

        MutablePicoContainer parent = new DefaultPicoContainer(grandParent);

        MutablePicoContainer child = new DefaultPicoContainer(parent);
        child.registerComponent(DependsOnTouchable.class, DependsOnTouchable.class,
                new Parameter[]{new ComponentParameter(Touchable.class)});

        assertNotNull(child.getComponent(DependsOnTouchable.class));
    }
}
