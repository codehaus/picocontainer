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

/**
 * @author Thomas Heller
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ComponentKeysTestCase extends TestCase {
    public void testComponensRegisteredWithClassKeyTakePrecedenceOverOthersWhenThereAreMultipleImplementations() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation("default", SimpleTouchable.class);

        /**
         * By using a class as key, this should take precedence over the other Touchable (Simmpe)
         */
        pico.registerComponentImplementation(Touchable.class, DecoratedTouchable.class, new Parameter[]{
            new ComponentParameter("default")
        });

        Touchable touchable = (Touchable) pico.getComponentInstanceOfType(Touchable.class);
        assertEquals(DecoratedTouchable.class, touchable.getClass());
    }

    public void testComponentAdapterResolutionIsFirstLookedForByClassKeyToTheTopOfTheContainerHierarchy() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation("default", SimpleTouchable.class);

        pico.registerComponentImplementation(Touchable.class, DecoratedTouchable.class, new Parameter[]{
            new ComponentParameter("default")
        });

        DefaultPicoContainer grandChild = new DefaultPicoContainer(new DefaultPicoContainer(new DefaultPicoContainer(pico)));

        Touchable touchable = (Touchable) grandChild.getComponentInstanceOfType(Touchable.class);
        assertEquals(DecoratedTouchable.class, touchable.getClass());

    }

}
