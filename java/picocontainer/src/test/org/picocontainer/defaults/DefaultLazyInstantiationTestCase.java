package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.tck.AbstractLazyInstantiationTestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultLazyInstantiationTestCase extends AbstractLazyInstantiationTestCase {
    protected MutablePicoContainer createPicoContainer() {
        return new DefaultPicoContainer();
    }
}
