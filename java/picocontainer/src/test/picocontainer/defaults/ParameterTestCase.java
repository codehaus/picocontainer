package picocontainer.defaults;

import junit.framework.TestCase;
import picocontainer.hierarchical.HierarchicalPicoContainer;
import picocontainer.testmodel.Wilma;
import picocontainer.testmodel.WilmaImpl;
import picocontainer.testmodel.FredImpl;
import picocontainer.*;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public class ParameterTestCase extends TestCase {
    public void testComponentSpecificationIsAssignableFrom() {
        assertTrue(ComponentSpecification.isAssignableFrom(Integer.class, Integer.TYPE));
        assertTrue(ComponentSpecification.isAssignableFrom(Integer.TYPE, Integer.class));
        assertTrue(ComponentSpecification.isAssignableFrom(String.class, String.class));
        assertFalse(ComponentSpecification.isAssignableFrom(Integer.class, String.class));
    }

    public void testComponentParameter() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        ClassRegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();
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

    public void testFredWithWilmaSpecifiedAsConstant() throws PicoRegistrationException, PicoInstantiationException, PicoIntrospectionException {
        ClassRegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();
        WilmaImpl wilma = new WilmaImpl();
        pico.registerComponent(FredImpl.class, FredImpl.class, new Parameter[]{
            new ConstantParameter(wilma)
        });
        pico.instantiateComponents();
        assertTrue(wilma.helloCalled());
    }

}
