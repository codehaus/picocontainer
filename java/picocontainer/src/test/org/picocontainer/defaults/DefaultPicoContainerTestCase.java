/*****************************************************************************
 * Copyright (C) OldPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.tck.AbstractPicoContainerTestCase;

public class DefaultPicoContainerTestCase extends AbstractPicoContainerTestCase {

    protected MutablePicoContainer createPicoContainer() {
        return new DefaultPicoContainer();
    }

    public void testAmbiguousResolution() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = (DefaultPicoContainer) createPicoContainer();
        pico.registerComponentImplementation("ping", String.class);
        pico.registerComponentInstance("pong", "pang");
        try {
            Object huh = pico.findComponentInstance(String.class);
        } catch (AmbiguousComponentResolutionException e) {
            assertTrue(e.getMessage().indexOf("java.lang.String") != -1);
        }
    }

    public void testNoResolution() throws PicoIntrospectionException, PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        DefaultPicoContainer pico = (DefaultPicoContainer) createPicoContainer();
        assertNull(pico.findComponentInstance(String.class));
    }
}
