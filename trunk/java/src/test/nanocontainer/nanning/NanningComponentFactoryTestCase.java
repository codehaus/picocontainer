/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirsén                                               *
 *****************************************************************************/

package nanocontainer.nanning;

import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.InterceptorAspect;
import junit.framework.TestCase;
import picocontainer.DuplicateComponentClassRegistrationException;
import picocontainer.DuplicateComponentTypeRegistrationException;
import picocontainer.NotConcreteRegistrationException;
import picocontainer.PicoContainer;
import picocontainer.PicoContainerImpl;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoStartException;
import picocontainer.WrongNumberOfConstructorsRegistrationException;

import java.util.ArrayList;

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

    private ArrayList log = new ArrayList();

    /**
     * Acceptance test (ie a teeny bit functional, but you'll get over it).
     */
    public void testSimpleLogOfMethodCall() throws PicoRegistrationException, PicoStartException {

        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new InterceptorAspect(new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                log.add(invocation.getMethod().getName());
                return invocation.invokeNext();
            }
        }));

        NanningComponentFactory componentFactory = new NanningComponentFactory(aspectSystem);
        PicoContainer nanningEnabledPicoContainer = new PicoContainerImpl.WithComponentFactory(componentFactory);
        nanningEnabledPicoContainer.registerComponent(WilmaImpl.class);
        nanningEnabledPicoContainer.registerComponent(FredImpl.class);

        assertEquals(0, log.size());

        nanningEnabledPicoContainer.start();

        assertEquals(1, log.size());

        // fred says hello to wilma, even the interceptor knows
        assertEquals("hello", log.get(0));

//        WilmaImpl wilma = (WilmaImpl) nanningEnabledPicoContainer.getComponent(WilmaImpl.class);
//
//        System.out.println("-->" + nanningEnabledPicoContainer.getComponents().length);
//        System.out.println("-->" + nanningEnabledPicoContainer.getComponents()[0]);
//        System.out.println("-->" + nanningEnabledPicoContainer.getComponents()[1]);
//
//
//        assertNotNull(wilma);
//
//        wilma.hello();
//
//        assertEquals(2, log.size());
//
//        assertEquals("hello", log.get(1));

    }

}
