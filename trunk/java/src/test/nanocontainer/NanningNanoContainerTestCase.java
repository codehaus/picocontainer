package nanocontainer;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.InterceptorAspect;
import junit.framework.TestCase;
import picocontainer.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Jon Tirsen
 * @version $Revision$
 */
public class NanningNanoContainerTestCase extends TestCase {

    /**
     * @author Jon Tirsen
     * @version $Revision$
     */
    public static class NanningPicoContainer extends PicoContainerImpl {
        public NanningPicoContainer(final AspectSystem aspectSystem) {
            super(new DummyContainer(), new DummyStartableLifecycleManager(), new DefaultComponentFactory() {
                public Object createComponent(Class compType, Constructor constructor, Object[] args)
                        throws InvocationTargetException, IllegalAccessException, InstantiationException
                {
                    Object component = super.createComponent(compType, constructor, args);
                    Class[] interfaces = compType.getInterfaces();

                    // Nanning will only aspectify stuff that has one and only one interface
                    if (interfaces.length == 1) {
                        Class interfaze = interfaces[0];

                        // the trick: set up first mixin manually with the component as target
                        AspectInstance aspectInstance = new AspectInstance();
                        MixinInstance mixin = new MixinInstance(interfaze, component);
                        aspectInstance.addMixin(mixin);

                        // let the aspects do it's work
                        aspectSystem.initialize(aspectInstance);
                        component = aspectInstance.getProxy();
                    }

                    return component;
                }
            });
        }
    }

    public interface Wilma {
        void hello();
    }

    public static class WilmaImpl implements Wilma {

        private boolean helloCalled;

        public boolean helloCalled() {
            return helloCalled;
        }

        public void hello()
        {
            helloCalled = true;
        }
    }

    public static class FredImpl {
        public FredImpl( Wilma wilma ) {
            assertNotNull("Wilma cannot be passed in as null", wilma);
            wilma.hello();
        }
    }

    private StringBuffer log = new StringBuffer();

    /**
     * Acceptance test (ie a teeny bit functional, but you'll get over it).
     */
    public void testAcceptance() throws AssignabilityRegistrationException,
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

        NanningPicoContainer nanningPicoContainer = new NanningPicoContainer(aspectSystem);
        nanningPicoContainer.registerComponent(WilmaImpl.class);
        nanningPicoContainer.registerComponent(FredImpl.class);

        assertEquals("", log.toString());

        nanningPicoContainer.start();

        // fred says hello to wilma, even the interceptor knows
        assertEquals("hello", log.toString());
    }


}
