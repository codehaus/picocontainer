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

/**
 * @author Jon Tirsen
 * @version $Revision: 1.3 $
 */
public class NanningComponentFactoryTestCase extends TestCase {

    public interface Wilma {
        void hello();
    }

    public static class WilmaImpl implements Wilma {

        private boolean helloCalled;

        public boolean helloCalled() {
            return helloCalled;
        }

        public void hello() {
            helloCalled = true;
        }
    }

    public static class FredImpl {
        public FredImpl(Wilma wilma) {
            assertNotNull("Wilma cannot be passed in as null", wilma);
            wilma.hello();
        }
    }

    private StringBuffer log = new StringBuffer();

    /**
     * Acceptance test (ie a teeny bit functional, but you'll get over it).
     */
    public void testAcceptance() throws PicoRegistrationException,
            NotConcreteRegistrationException,
            WrongNumberOfConstructorsRegistrationException,
            PicoStartException,
            DuplicateComponentTypeRegistrationException,
            DuplicateComponentClassRegistrationException {
        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new InterceptorAspect(new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                log.append(invocation.getMethod().getName());
                return invocation.invokeNext();
            }
        }));

        NanningComponentFactory componentFactory = new NanningComponentFactory(aspectSystem);
        PicoContainer nanningPicoContainer = new PicoContainerImpl.WithComponentFactory(componentFactory);
        nanningPicoContainer.registerComponent(WilmaImpl.class);
        nanningPicoContainer.registerComponent(FredImpl.class);

        assertEquals("", log.toString());

        nanningPicoContainer.start();

        // fred says hello to wilma, even the interceptor knows
        assertEquals("hello", log.toString());
    }

}
