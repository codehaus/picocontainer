package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.internals.*;
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

//  I don't think this is something we need to assert anymore, as the
//  getParameters() now does it lazily. And the parameters can't
//
//    public void testComponentAdapterCreatesDefaultParameters() throws PicoIntrospectionException {
//        DefaultComponentAdapter componentAdapter =
//                new DefaultComponentAdapter(null, TestClass.class);
//        assertEquals(3, componentAdapter.getParameters().length);
//    }

    public void testComponentParameterFetches() throws PicoInstantiationException, PicoRegistrationException, PicoInitializationException {
        DefaultComponentRegistry dcr = new DefaultComponentRegistry();
        DefaultPicoContainer pico = new DefaultPicoContainer.WithComponentRegistry(dcr);
        pico.registerComponent(Touchable.class, SimpleTouchable.class);
        ComponentParameter parameter = new ComponentParameter(Touchable.class);

        assertNotNull(pico.getComponent(Touchable.class));
        Touchable touchable = (Touchable) parameter.resolveAdapter(dcr).instantiateComponent(dcr);
        assertNotNull(touchable);
    }

    public void testConstantParameter() throws PicoInitializationException {
        Object value = new Object();
        ConstantParameter parameter = new ConstantParameter(value);
        assertSame(value, parameter.resolveAdapter(null).instantiateComponent(null));
    }

    public void testDependsOnTouchableWithTouchableSpecifiedAsConstant() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        SimpleTouchable touchable = new SimpleTouchable();
        pico.registerComponent(DependsOnTouchable.class, DependsOnTouchable.class, new Parameter[]{
            new ConstantParameter(touchable)
        });
        pico.getComponents();
        assertTrue(touchable.wasTouched);
    }

}
