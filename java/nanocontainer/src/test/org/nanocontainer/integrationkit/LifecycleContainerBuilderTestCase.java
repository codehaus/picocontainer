package org.picoextras.integrationkit;

import junit.framework.TestCase;
import org.jmock.Mock;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.picocontainer.lifecycle.Startable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class LifecycleContainerBuilderTestCase extends TestCase {
    public void testBuildContainerCreatesANewChildContainerAndStartsItButNotTheParent() {
        final Mock childStartable = new Mock(Startable.class);
        childStartable.expect("start");

        LifecycleContainerBuilder builder = new LifecycleContainerBuilder();
        ContainerAssembler containerAssembler = new ContainerAssembler() {
            public void assembleContainer(MutablePicoContainer container, Object assemblyScope) {
                container.registerComponentInstance(childStartable.proxy());
            }
        };

        ObjectReference parentRef = new SimpleReference();
        MutablePicoContainer parentC = new DefaultPicoContainer();

        // Expect no calls on this one!
        Mock parentStartable = new Mock(Startable.class);
        parentC.registerComponentInstance(parentStartable.proxy());
        parentRef.set(parentC);

        ObjectReference childRef = new SimpleReference();

        builder.buildContainer(childRef, parentRef, containerAssembler, null);
        PicoContainer childContainer = (PicoContainer) childRef.get();
        assertSame(parentC, childContainer.getParent());

        builder.killContainer(childRef);
        assertNull(childContainer.getParent());

        parentStartable.verify();
        childStartable.verify();
    }

}
