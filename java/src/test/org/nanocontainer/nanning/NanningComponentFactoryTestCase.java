/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirs?n                                               *
 *****************************************************************************/

package org.nanocontainer.nanning;

import junit.framework.TestCase;
import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.config.AspectSystem;
import org.codehaus.nanning.config.InterceptorAspect;
import org.codehaus.nanning.config.Aspect;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.defaults.ComponentSpecification;
import org.picocontainer.defaults.DefaultComponentFactory;
import org.picocontainer.hierarchical.HierarchicalPicoContainer;

/**
 * @author Jon Tirsen
 * @version $Revision: 1.3 $
 */
public class NanningComponentFactoryTestCase extends TestCase {

    public interface Wilma {
        void hello();
    }

    public static class WilmaImpl implements Wilma {
        public void hello() {
        }
    }

    public static class FredImpl {
        public FredImpl(Wilma wilma) {
            assertNotNull("Wilma cannot be passed in as null", wilma);
            wilma.hello();
        }
    }

    private StringBuffer log = new StringBuffer();

    public void testComponentsWithOneInterfaceAreAspected() throws PicoInitializationException {
        NanningComponentFactory componentFactory = new NanningComponentFactory(new AspectSystem(), new DefaultComponentFactory());
        Object component = componentFactory.createComponent(new ComponentSpecification(componentFactory, Wilma.class, WilmaImpl.class), null);
        assertTrue(Aspects.isAspectObject(component));
        assertEquals(Wilma.class, Aspects.getAspectInstance(component).getClassIdentifier());
    }

    public void testComponentsWithoutInterfaceNotAspected() throws PicoInitializationException {
        NanningComponentFactory componentFactory = new NanningComponentFactory(new AspectSystem(), new DefaultComponentFactory());
        Object component = componentFactory.createComponent(
                new ComponentSpecification(componentFactory, FredImpl.class, FredImpl.class),
                new Object[] { new WilmaImpl() });
        assertFalse(Aspects.isAspectObject(component));
    }


    /**
     * Acceptance test (ie a teeny bit functional, but you'll get over it).
     */
    public void testSimpleLogOfMethodCall()
            throws PicoRegistrationException, PicoInitializationException {

        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new InterceptorAspect(new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                log.append(invocation.getMethod().getName() + " ");
                return invocation.invokeNext();
            }
        }));

        NanningComponentFactory componentFactory = new NanningComponentFactory(aspectSystem, new DefaultComponentFactory());
        RegistrationPicoContainer nanningEnabledPicoContainer = new HierarchicalPicoContainer.WithComponentFactory(
                componentFactory);
        nanningEnabledPicoContainer.registerComponent(Wilma.class, WilmaImpl.class);
        nanningEnabledPicoContainer.registerComponentByClass(FredImpl.class);

        assertEquals("", log.toString());

        nanningEnabledPicoContainer.instantiateComponents();

        // fred says hello to wilma, even the interceptor knows
        assertEquals("hello ", log.toString());

        Wilma wilma = (Wilma) nanningEnabledPicoContainer.getComponent(Wilma.class);

        assertNotNull(wilma);

        wilma.hello();

        // another entry in the log
        assertEquals("hello hello ", log.toString());

    }

}
