/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirs?n                                               *
 *****************************************************************************/

package nanocontainer.nanning;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.InterceptorAspect;
import junit.framework.TestCase;
import picocontainer.ClassRegistrationPicoContainer;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoInvocationTargetInitailizationException;
import picocontainer.hierarchical.HierarchicalPicoContainer;

import java.lang.reflect.InvocationTargetException;

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

    public void testComponentsWithInterfaceAsTypeAreAspected() throws PicoInvocationTargetInitailizationException {
        NanningComponentFactory componentFactory = new NanningComponentFactory(new AspectSystem());
        Object component = componentFactory.createComponent(Wilma.class, WilmaImpl.class.getConstructors()[0], null);
        assertTrue(Aspects.isAspectObject(component));
    }

    public void testComponentsWithoutInterfaceAsTypeAreNotAspected() throws PicoInvocationTargetInitailizationException {
        NanningComponentFactory componentFactory = new NanningComponentFactory(new AspectSystem());
        Object component = componentFactory.createComponent(WilmaImpl.class, WilmaImpl.class.getConstructors()[0],
                null);
        assertFalse(Aspects.isAspectObject(component));
    }


    /**
     * Acceptance test (ie a teeny bit functional, but you'll get over it).
     */
    public void testSimpleLogOfMethodCall() throws PicoRegistrationException, PicoInstantiationException {

        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new InterceptorAspect(new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                log.append(invocation.getMethod().getName() + " ");
                return invocation.invokeNext();
            }
        }));

        NanningComponentFactory componentFactory = new NanningComponentFactory(aspectSystem);
        ClassRegistrationPicoContainer nanningEnabledPicoContainer = new HierarchicalPicoContainer.WithComponentFactory(
                componentFactory);
        nanningEnabledPicoContainer.registerComponent(Wilma.class, WilmaImpl.class);
        nanningEnabledPicoContainer.registerComponent(FredImpl.class);

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
