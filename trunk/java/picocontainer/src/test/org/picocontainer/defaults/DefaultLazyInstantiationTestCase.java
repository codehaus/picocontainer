package org.picocontainer.defaults;

import org.picocontainer.tck.AbstractLazyInstantiationTestCase;
import org.picocontainer.RegistrationPicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultLazyInstantiationTestCase extends AbstractLazyInstantiationTestCase {
    protected RegistrationPicoContainer createRegistrationPicoContainer() {
        return new DefaultPicoContainer.Default();
    }
}
