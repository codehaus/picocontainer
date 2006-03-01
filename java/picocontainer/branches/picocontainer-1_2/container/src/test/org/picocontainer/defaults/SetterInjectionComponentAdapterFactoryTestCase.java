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
import org.picocontainer.PicoInitializationException;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.tck.AbstractComponentAdapterTestCase.RecordingLifecycleStrategy;
import org.picocontainer.testmodel.NullLifecycle;
import org.picocontainer.testmodel.RecordingLifecycle;
import org.picocontainer.testmodel.RecordingLifecycle.One;

/**
 * @author J&ouml;rg Schaible</a>
 * @version $Revision$
 */
public class SetterInjectionComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer(createComponentAdapterFactory());
    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new SetterInjectionComponentAdapterFactory();
    }

    public static interface Bean {
    }

    public static class NamedBean implements Bean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class NamedBeanWithPossibleDefault extends NamedBean {
        private boolean byDefault;

        public NamedBeanWithPossibleDefault() {
        }

        public NamedBeanWithPossibleDefault(String name) {
            setName(name);
            byDefault = true;
        }

        public boolean getByDefault() {
            return byDefault;
        }
    }

    public static class NoBean extends NamedBean {
        public NoBean(String name) {
            setName(name);
        }
    }

    public void testContainerUsesStandardConstructor() {
        picoContainer.registerComponentImplementation(Bean.class, NamedBeanWithPossibleDefault.class);
        picoContainer.registerComponentInstance("Tom");
        NamedBeanWithPossibleDefault bean = (NamedBeanWithPossibleDefault) picoContainer.getComponentInstance(Bean.class);
        assertFalse(bean.getByDefault());
    }

    public void testContainerUsesOnlyStandardConstructor() {
        picoContainer.registerComponentImplementation(Bean.class, NoBean.class);
        picoContainer.registerComponentInstance("Tom");
        try {
            picoContainer.getComponentInstance(Bean.class);
            fail("Instantiation should have failed.");
        } catch (PicoInitializationException e) {
        }
    }

    public void testCustomLifecycleCanBeInjected() throws NoSuchMethodException {
        RecordingLifecycleStrategy strategy = new RecordingLifecycleStrategy(new StringBuffer());
        SetterInjectionComponentAdapterFactory caf = new SetterInjectionComponentAdapterFactory(false, strategy);
        SetterInjectionComponentAdapter sica = (SetterInjectionComponentAdapter)caf.createComponentAdapter(NullLifecycle.class, NullLifecycle.class, new Parameter[0]);
        One one = new RecordingLifecycle.One(new StringBuffer());
        sica.start(one);
        sica.stop(one);        
        sica.dispose(one);
        assertEquals("<start<stop<dispose", strategy.recording());
    }    
}