/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.hierarchical;

import junit.framework.TestCase;
import picocontainer.PicoInitializationException;
import picocontainer.PicoRegistrationException;
import picocontainer.testmodel.FredImpl;
import picocontainer.testmodel.WilmaImpl;

public class ComponentInteroperationTestCase extends TestCase {

    public void testBasic() throws PicoInitializationException, PicoRegistrationException
    {

        WilmaImpl wilma = new WilmaImpl();
        OverriddenPicoTestContainer pico = new OverriddenPicoTestContainer(wilma);

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);

        pico.instantiateComponents();

        assertTrue("hello should have been called in wilma", wilma.helloCalled());
    }


}
