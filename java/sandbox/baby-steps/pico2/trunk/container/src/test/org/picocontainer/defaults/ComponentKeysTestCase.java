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
import org.picocontainer.Parameter;
import org.picocontainer.testmodel.DecoratedTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.DependsOnTouchable;

import java.util.Collections;

/**
 * @author Thomas Heller
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ComponentKeysTestCase extends TestCase {
    public void testComponensRegisteredWithClassKeyTakePrecedenceOverOthersWhenThereAreMultipleImplementations() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent("default", SimpleTouchable.class);

        /**
         * By using a class as key, this should take precedence over the other Touchable
         */
        pico.registerComponent(Touchable.class, DecoratedTouchable.class, new Parameter[]{
                            new ComponentParameter("default")
                    });

        Touchable touchable = (Touchable) pico.getComponent(Touchable.class);
        assertEquals(DecoratedTouchable.class, touchable.getClass());
    }

    public void testComponentAdapterResolutionIsFirstLookedForByClassKeyToTheTopOfTheContainerHierarchy() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent("default", SimpleTouchable.class);

        // Use the List variant instead, so we get better test coverage.
        pico.registerComponentImplementation(Touchable.class, DecoratedTouchable.class, Collections.singletonList(new ComponentParameter("default")));

        DefaultPicoContainer grandChild = new DefaultPicoContainer(new DefaultPicoContainer(new DefaultPicoContainer(pico)));

        Touchable touchable = (Touchable) grandChild.getComponent(Touchable.class);
        assertEquals(DecoratedTouchable.class, touchable.getClass());

    }

    public void testComponentKeysFromParentCannotConfuseTheChild() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent("test", SimpleTouchable.class);

        DefaultPicoContainer child = new DefaultPicoContainer(pico);

        child.registerComponent("test", DependsOnTouchable.class);

        DependsOnTouchable dot = (DependsOnTouchable) child.getComponent("test");

        assertNotNull(dot);
    }

}
