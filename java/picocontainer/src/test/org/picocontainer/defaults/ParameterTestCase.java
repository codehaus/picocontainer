package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.*;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;


/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision: 1.7 $
 */
public class ParameterTestCase extends TestCase {
    public void testComponentSpecificationHandlesPrimtiveTypes() {
        assertTrue(TransientComponentAdapter.isAssignableFrom(Integer.class, Integer.TYPE));
        assertTrue(TransientComponentAdapter.isAssignableFrom(Integer.TYPE, Integer.class));
        assertTrue(TransientComponentAdapter.isAssignableFrom(String.class, String.class));
        assertTrue(TransientComponentAdapter.isAssignableFrom(Double.TYPE, Double.class));
        assertTrue(TransientComponentAdapter.isAssignableFrom(Long.TYPE, Long.class));
        assertTrue(TransientComponentAdapter.isAssignableFrom(Short.TYPE, Short.class));
        assertTrue(TransientComponentAdapter.isAssignableFrom(Float.TYPE, Float.class));
        assertTrue(TransientComponentAdapter.isAssignableFrom(Byte.TYPE, Byte.class));
        assertTrue(TransientComponentAdapter.isAssignableFrom(Character.TYPE, Character.class));
        assertTrue(TransientComponentAdapter.isAssignableFrom(Boolean.TYPE, Boolean.class));
        assertFalse(TransientComponentAdapter.isAssignableFrom(Integer.class, String.class));
        assertFalse(TransientComponentAdapter.isAssignableFrom(Double.class, String.class));
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
