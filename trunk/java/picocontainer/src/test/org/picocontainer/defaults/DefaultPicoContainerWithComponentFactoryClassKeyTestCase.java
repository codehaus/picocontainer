package org.picocontainer.defaults;

import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.tck.AbstractBasicClassCompatabilityTestCase;

public class DefaultPicoContainerWithComponentFactoryClassKeyTestCase extends AbstractBasicClassCompatabilityTestCase {
    protected RegistrationPicoContainer createClassRegistrationPicoContainer() {
        return new DefaultPicoContainer.Default();
    }
}
