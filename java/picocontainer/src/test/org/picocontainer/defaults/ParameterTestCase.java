package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.hierarchical.HierarchicalPicoContainer;
import org.picocontainer.testmodel.Wilma;
import org.picocontainer.testmodel.WilmaImpl;
import org.picocontainer.testmodel.FredImpl;
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
        RegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();
        pico.registerComponent(Wilma.class, WilmaImpl.class);
        ComponentParameter parameter = new ComponentParameter();

        assertNull(pico.getComponent(Wilma.class));
        Wilma wilma = (Wilma) parameter.resolve(pico, null, Wilma.class);
        assertNotNull(pico.getComponent(Wilma.class));
        assertSame(wilma, pico.getComponent(Wilma.class));
    }

    public void testConstantParameter() throws PicoInstantiationException {
        Object value = new Object();
        ConstantParameter parameter = new ConstantParameter(value);
        assertSame(value, parameter.resolve(null, null, null));
    }

    public void testFredWithWilmaSpecifiedAsConstant() throws PicoRegistrationException, PicoInitializationException {
        RegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();
        WilmaImpl wilma = new WilmaImpl();
        pico.registerComponent(FredImpl.class, FredImpl.class, new Parameter[] {
            new ConstantParameter(wilma)
        });
        pico.instantiateComponents();
        assertTrue(wilma.helloCalled());
    }

}
