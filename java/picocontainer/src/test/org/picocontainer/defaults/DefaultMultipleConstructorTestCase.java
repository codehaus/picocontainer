package org.picocontainer.defaults;

import org.picocontainer.tck.AbstractMultipleConstructorTestCase;
import org.picocontainer.MutablePicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultMultipleConstructorTestCase extends AbstractMultipleConstructorTestCase {
    protected MutablePicoContainer createPicoContainer() {
        return new DefaultPicoContainer();
    }
}
