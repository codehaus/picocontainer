/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

import junit.framework.TestCase;
import picocontainer.PicoContainerImpl;
import picocontainer.DuplicateComponentTypeRegistrationException;
import picocontainer.PicoContainer;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoStartException;
import picocontainer.UnsatisfiedDependencyStartupException;
import picocontainer.testmodel.FredImpl;
import picocontainer.testmodel.WilmaImpl;

public class ComponentInteroperationTestCase extends TestCase {

    public void testBasic() throws PicoStartException, PicoRegistrationException
    {

        WilmaImpl wilma = new WilmaImpl();
        PicoContainer pico = new OverriddenPicoTestContainer(wilma, new NullStartableLifecycleManager());

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);

        pico.start();

        assertTrue("hello should have been called in wilma", wilma.helloCalled());
    }


}
