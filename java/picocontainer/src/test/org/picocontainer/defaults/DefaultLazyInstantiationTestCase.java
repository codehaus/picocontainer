package org.picocontainer.defaults;

import org.picocontainer.tck.AbstractLazyInstantiationTestCase;
import org.picocontainer.MutablePicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultLazyInstantiationTestCase extends AbstractLazyInstantiationTestCase {
    protected MutablePicoContainer createPicoContainer() {
        return new DefaultPicoContainer();
    }
}
