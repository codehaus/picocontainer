package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.*;


/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision: 1.7 $
 */
public class ParameterTestCase extends TestCase {
    public void testComponentSpecificationHandlesPrimtiveTypes() {
        assertTrue(DefaultComponentAdapter.isAssignableFrom(Integer.class, Integer.TYPE));
        assertTrue(DefaultComponentAdapter.isAssignableFrom(Integer.TYPE, Integer.class));
        assertTrue(DefaultComponentAdapter.isAssignableFrom(String.class, String.class));
        assertFalse(DefaultComponentAdapter.isAssignableFrom(Integer.class, String.class));
    }

    static class TestClass {
        public TestClass(String s1, String s2, String s3) {
        }
    }

    public void testComponentParameterFetches() throws PicoInstantiationException, PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        ComponentParameter parameter = new ComponentParameter(Touchable.class);

        assertNotNull(pico.getComponentInstance(Touchable.class));
        Touchable touchable = (Touchable) parameter.resolveAdapter(pico).getComponentInstance(pico);
        assertNotNull(touchable);
    }

    public void testConstantParameter() throws PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        Object value = new Object();
        ConstantParameter parameter = new ConstantParameter(value);
        assertSame(value, parameter.resolveAdapter(null).getComponentInstance(null));
    }

    public void testDependsOnTouchableWithTouchableSpecifiedAsConstant() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        SimpleTouchable touchable = new SimpleTouchable();
        pico.registerComponentImplementation(DependsOnTouchable.class, DependsOnTouchable.class, new Parameter[]{
            new ConstantParameter(touchable)
        });
        pico.getComponentInstances();
        assertTrue(touchable.wasTouched);
    }

}
