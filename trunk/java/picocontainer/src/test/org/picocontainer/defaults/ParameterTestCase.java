package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.tck.Touchable;
import org.picocontainer.tck.SimpleTouchable;
import org.picocontainer.tck.DependsOnTouchable;
import org.picocontainer.*;


/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision: 1.7 $
 */
public class ParameterTestCase extends TestCase {
    public void testComponentSpecificationHandlesPrimtiveTypes() {
        assertTrue(ComponentSpecification.isAssignableFrom(Integer.class, Integer.TYPE));
        assertTrue(ComponentSpecification.isAssignableFrom(Integer.TYPE, Integer.class));
        assertTrue(ComponentSpecification.isAssignableFrom(String.class, String.class));
        assertFalse(ComponentSpecification.isAssignableFrom(Integer.class, String.class));
    }

    static class TestClass {
        public TestClass(String s1, String s2, String s3) {}
    }

    public void testComponentSpecificationCreatesDefaultParameters() throws PicoIntrospectionException {
        ComponentSpecification componentSpec =
                new ComponentSpecification(new DefaultComponentFactory(), null, TestClass.class);
        assertEquals(3, componentSpec.getParameters().length);
    }

    public void testComponentParameterFetches() throws PicoInstantiationException, PicoRegistrationException, PicoInitializationException {
        DefaultComponentRegistry dcr = new DefaultComponentRegistry();
        DefaultPicoContainer pico = new DefaultPicoContainer.WithComponentRegistry(dcr);
        pico.registerComponent(Touchable.class, SimpleTouchable.class);
        ComponentParameter parameter = new ComponentParameter();

        assertNull(pico.getComponent(Touchable.class));
        Touchable Touchable = (Touchable) parameter.resolve(dcr, null, Touchable.class);
        assertNotNull(pico.getComponent(Touchable.class));
        assertSame(Touchable, pico.getComponent(Touchable.class));
    }

    public void testConstantParameter() throws PicoInstantiationException {
        Object value = new Object();
        ConstantParameter parameter = new ConstantParameter(value);
        assertSame(value, parameter.resolve(null, null, null));
    }

    public void testFredWithTouchableSpecifiedAsConstant() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        SimpleTouchable touchable = new SimpleTouchable();
        pico.registerComponent(DependsOnTouchable.class, DependsOnTouchable.class, new Parameter[] {
            new ConstantParameter(touchable)
        });
        pico.instantiateComponents();
        assertTrue(touchable.wasTouched);
    }

}
