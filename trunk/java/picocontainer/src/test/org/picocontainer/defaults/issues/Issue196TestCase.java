package org.picocontainer.defaults.issues;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

public class Issue196TestCase extends TestCase {

    public void testShouldAllowRegistrationOfArrayAsInstance() {
        MutablePicoContainer pico = new DefaultPicoContainer();

        Descriptor.DescriptorData[] datas = new Descriptor.DescriptorData[3];

        pico.registerComponentInstance(datas);
        pico.registerComponentImplementation(DescriptorDep.class);

        DescriptorDep descriptorDep = (DescriptorDep) pico.getComponentInstanceOfType(DescriptorDep.class);

        assertNotNull(descriptorDep);
    }
}
