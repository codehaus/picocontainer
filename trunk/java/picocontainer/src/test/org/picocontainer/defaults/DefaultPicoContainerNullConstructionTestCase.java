package org.picocontainer.defaults;

import org.picocontainer.tck.AbstractNullConstructionTestCase;

public class DefaultPicoContainerNullConstructionTestCase extends AbstractNullConstructionTestCase {
    protected Class getContainerClass() {
        return DefaultPicoContainer.class;
    }

    protected Object[] getContainersInstantiationParameters() {
        return new Object[] {new DefaultComponentFactory(),new DefaultComponentRegistry()};
    }
}
