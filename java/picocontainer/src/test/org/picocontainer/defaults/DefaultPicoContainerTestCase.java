/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.*;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.tck.AbstractPicoContainerTestCase;

public class DefaultPicoContainerTestCase extends AbstractPicoContainerTestCase {

    protected MutablePicoContainer createPicoContainer() {
        return new DefaultPicoContainer();
    }

    // to go to parent testcase ?
    public void testBasicInstantiationAndContainment() throws PicoException, PicoRegistrationException {
        DefaultPicoContainer pico = (DefaultPicoContainer) createPicoContainerWithTouchableAndDependency();

        assertTrue("Component should be instance of Touchable",
                pico.findComponentInstance(Touchable.class) instanceof Touchable);
    }

}
