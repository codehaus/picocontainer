package org.picocontainer.tck;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.testmodel.Wilma;
import org.picocontainer.testmodel.FredImpl;
import org.picocontainer.testmodel.WilmaImpl;


public class DemonstrationCompatabilityTestCase extends AbstractBasicCompatabilityTestCase {

    protected void setUp() throws Exception {
        DefaultPicoContainer dftPico = new DefaultPicoContainer.Default();
        dftPico.registerComponent(Wilma.class, WilmaImpl.class);
        dftPico.registerComponentByClass(FredImpl.class);
        picoContainer = dftPico;
    }

    // testXXX methods are in superclass.

}
