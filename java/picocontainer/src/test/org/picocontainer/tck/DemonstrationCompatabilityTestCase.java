package org.picocontainer.tck;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.testmodel.WilmaImpl;
import org.nanocontainer.testmodel.Wilma;
import org.nanocontainer.testmodel.FredImpl;

public class DemonstrationCompatabilityTestCase extends AbstractBasicCompatabilityTestCase {

    protected void setUp() throws Exception {
        DefaultPicoContainer dftPico = new DefaultPicoContainer.Default();
        dftPico.registerComponent(Wilma.class, WilmaImpl.class);
        dftPico.registerComponentByClass(FredImpl.class);
        picoContainer = dftPico;
    }

    // testXXX methods are in superclass.

}
