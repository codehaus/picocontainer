package org.nanocontainer.nanowar;

import java.io.Serializable;
import org.picocontainer.PicoContainer;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.DefaultLifecycleContainerBuilder;

/**
 * Base class for application-level and session-level listeners.
 *
 * @author Michael Rimov
 */
public class AbstractNanoWarListener implements Serializable {

    protected PicoContainer buildContainer(ScriptedContainerBuilder builder) {
        ObjectReference containerRef = new SimpleReference();
        builder.buildContainer(containerRef, new SimpleReference(), new SimpleReference(), false);
        return (PicoContainer) containerRef.get();
    }

    protected void killContainer(ObjectReference containerRef) {
        ContainerBuilder containerKiller = new DefaultLifecycleContainerBuilder(null);
        if (containerRef.get() != null) {
            containerKiller.killContainer(containerRef);
        }
    }
}
