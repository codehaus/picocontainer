/*****************************************************************************
 * Copyright (C) OldPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.extras;

import junit.framework.TestCase;
import org.picocontainer.*;
import org.picocontainer.defaults.*;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.Touchable;

import java.io.Serializable;

public class DecoratingPicoContainerTestCase extends TestCase {
    private AbstractPicoContainer london;
    private AbstractPicoContainer bangalore;
    private DecoratingPicoContainer globe;

    public void setUp() throws PicoRegistrationException, PicoInitializationException {
        london = new DefaultPicoContainer();
        london.registerComponentImplementation(DependsOnTouchable.class);

        bangalore = new DefaultPicoContainer();
        bangalore.registerComponentImplementation(SimpleTouchable.class);

        globe = new DecoratingPicoContainer();
    }

    public void testSuccessfulSimpleDelegation() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        globe.addDelegate(bangalore);
        assertNotNull(globe.getComponentInstance(SimpleTouchable.class));
    }

    public void testFailingSimpleDelegation() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        globe.addDelegate(london);
        try {
            globe.getComponentInstance(DependsOnTouchable.class);
            fail();
        } catch (NoSatisfiableConstructorsException e) {
        }
    }

    public void testMultiDelegation() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        globe.addDelegate(london);
        globe.addDelegate(bangalore);
        assertNotNull(globe.getComponentInstance(DependsOnTouchable.class));
    }

    public void testMulticaster() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        globe.addDelegate(london);
        globe.addDelegate(bangalore);

        Object multicaster = globe.getComponentMulticaster();
        assertTrue(multicaster instanceof Serializable);
        assertTrue(multicaster instanceof Touchable);
    }
}
