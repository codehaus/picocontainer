/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.junit.Before;
import org.junit.Test;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.tck.AbstractComponentFactoryTest;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * @author J&ouml;rg Schaible
 */
public class SetterInjectionTestCase extends AbstractComponentFactoryTest {
	
	@Before
    public void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer(createComponentFactory());
    }

    protected ComponentFactory createComponentFactory() {
        return new SetterInjection();
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

    @Test public void testContainerUsesStandardConstructor() {
        picoContainer.addComponent(Bean.class, NamedBeanWithPossibleDefault.class);
        picoContainer.addComponent("Tom");
        NamedBeanWithPossibleDefault bean = (NamedBeanWithPossibleDefault) picoContainer.getComponent(Bean.class);
        assertFalse(bean.getByDefault());
    }

    @Test public void testContainerUsesOnlyStandardConstructor() {
        picoContainer.addComponent(Bean.class, NoBean.class);
        picoContainer.addComponent("Tom");
        try {
            picoContainer.getComponent(Bean.class);
            fail("Instantiation should have failed.");
        } catch (PicoCompositionException e) {
        }
    }

    public static class AnotherNamedBean implements Bean {
        private String name;

        public String getName() {
            return name;
        }

        public void initName(String name) {
            this.name = name;
        }
    }

    @Test public void testAlternatePrefixWorks() {
        picoContainer = new DefaultPicoContainer(new SetterInjection("init"));
        picoContainer.addComponent(Bean.class, AnotherNamedBean.class);
        picoContainer.addComponent("Tom");
        AnotherNamedBean bean = picoContainer.getComponent(AnotherNamedBean.class);
        assertEquals("Tom", bean.getName());
    }

    public static class AnotherNamedBean2 extends AnotherNamedBean {
        private String name2;

        public String getName2() {
            return name2;
        }

        public void initName2(String name) {
            this.name2 = name;
        }
    }


    @Test public void testNotMatcherWorks() {
        picoContainer = new DefaultPicoContainer(new SetterInjection("init", "initName2"));
        picoContainer.addComponent(Bean.class, AnotherNamedBean2.class);
        picoContainer.addComponent("Tom");
        AnotherNamedBean2 bean = picoContainer.getComponent(AnotherNamedBean2.class);
        assertEquals("Tom", bean.getName());
        assertNull(bean.getName2());
    }


}