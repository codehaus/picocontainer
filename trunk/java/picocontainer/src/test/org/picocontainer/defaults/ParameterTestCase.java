package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.internals.*;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoInstantiationException;


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

    public void testComponentSpecificationCreatesDefaultParameters() throws PicoIntrospectionException {
        DefaultComponentAdapter componentSpec =
                new DefaultComponentAdapter(null, TestClass.class);
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
        pico.registerComponent(DependsOnTouchable.class, DependsOnTouchable.class, new Parameter[]{
            new ConstantParameter(touchable)
        });
        pico.instantiateComponents();
        assertTrue(touchable.wasTouched);
    }

}
