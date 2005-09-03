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

import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.tck.AbstractComponentAdapterTestCase.RecordingLifecycleStrategy;
import org.picocontainer.testmodel.NullLifecycle;

/**
 * @author Mauro Talevi
 * @version $Revision:  $
 */
public class ConstructorInjectionComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer(createComponentAdapterFactory());
    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new ConstructorInjectionComponentAdapterFactory();
    }

    public void testCustomLifecycleCanBeInjected() throws NoSuchMethodException {
        RecordingLifecycleStrategy strategy = new RecordingLifecycleStrategy(new StringBuffer());
        ConstructorInjectionComponentAdapterFactory caf = 
            new ConstructorInjectionComponentAdapterFactory(false, strategy);
        ConstructorInjectionComponentAdapter cica =  (ConstructorInjectionComponentAdapter)
        caf.createComponentAdapter(NullLifecycle.class, NullLifecycle.class, new Parameter[0]);
        PicoContainer pico = null;
        cica.start(pico);
        cica.stop(pico);
        cica.dispose(pico);
        assertEquals("<start<stop<dispose", strategy.recording());
    }    
}