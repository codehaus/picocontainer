package org.picocontainer.defaults;

import org.picocontainer.tck.AbstractMultipleConstructorTestCase;
import org.picocontainer.RegistrationPicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultMultipleConstructorTestCase extends AbstractMultipleConstructorTestCase {
    protected RegistrationPicoContainer createRegistrationPicoContainer() {
        return new DefaultPicoContainer.Default();
    }
}
