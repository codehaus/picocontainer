package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.tck.AbstractMultipleConstructorTestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultMultipleConstructorTestCase extends AbstractMultipleConstructorTestCase {
    protected MutablePicoContainer createPicoContainer() {
        return new DefaultPicoContainer();
    }
}
