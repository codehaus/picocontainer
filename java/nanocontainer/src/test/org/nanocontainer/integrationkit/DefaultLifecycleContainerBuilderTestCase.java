package org.picoextras.integrationkit;

import junit.framework.TestCase;
import org.jmock.C;
import org.jmock.Mock;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultLifecycleContainerBuilderTestCase extends TestCase {
    private class SimpleObjectReference implements ObjectReference {
        private Object item;

        public Object get() {
            return item;
        }

        public void set(Object item) {
            this.item = item;
        }
    }

    public void testBuildContainerCreatesANewChildContainer() {
        LifecycleContainerBuilder builder = new LifecycleContainerBuilder();
        Mock containerAssembler = new Mock(ContainerAssembler.class);
        containerAssembler.expect("assembleContainer", C.ANY_ARGS);

        SimpleObjectReference parentRef = new SimpleObjectReference();
        MutablePicoContainer parentC = new DefaultPicoContainer();
        parentRef.set(parentC);

        SimpleObjectReference childRef = new SimpleObjectReference();

        builder.buildContainer(childRef, parentRef, (ContainerAssembler) containerAssembler.proxy(), "test");
        PicoContainer childContainer = (PicoContainer) childRef.get();
        assertSame(parentC, childContainer.getParent());

        builder.killContainer(childRef);
        assertNull(childContainer.getParent());
    }

}
