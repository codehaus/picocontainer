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

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.internals.ComponentFactory;
import org.picocontainer.tck.AbstractBasicStringCompatabilityTestCase;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;

import java.util.Map;
import java.util.HashMap;

public class DefaultPicoContainerWithComponentFactoryStringKeyTestCase extends AbstractBasicStringCompatabilityTestCase {

    public PicoContainer createPicoContainerWithTouchableAndDependency() throws DuplicateComponentKeyRegistrationException,
            AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {

        ComponentFactory componentFactory = new DefaultComponentFactory();
        DefaultPicoContainer defaultPico = new DefaultPicoContainer.WithComponentFactory(componentFactory);
        defaultPico.registerComponent("touchable", SimpleTouchable.class);
        defaultPico.registerComponent("dependsOnTouchable", DependsOnTouchable.class);
        return defaultPico;
    }

    public PicoContainer createPicoContainerWithTouchablesDependancyOnly() throws PicoRegistrationException, PicoIntrospectionException {
        ComponentFactory componentFactory = new DefaultComponentFactory();
        DefaultPicoContainer defaultPico = new DefaultPicoContainer.WithComponentFactory(componentFactory);
        defaultPico.registerComponent("dependsOnTouchable", DependsOnTouchable.class);
        return defaultPico;
    }

    protected void addAHashMapByInstance(PicoContainer picoContainer) throws PicoRegistrationException, PicoIntrospectionException {
        ((DefaultPicoContainer) picoContainer).registerComponent("map", new HashMap());
    }

    // testXXX methods are in superclass.

}
