package org.picocontainer.alternatives;

import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;

import java.util.List;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class OldLifecycleManager implements LifecycleManager {

    public void start(PicoContainer node) {
        List startables = node.getComponentInstancesOfType(Startable.class);
        for (int i = 0; i < startables.size(); i++) {
            ((Startable) startables.get(i)).start();
        }
    }

    public void stop(PicoContainer node) {
        List startables = node.getComponentInstancesOfType(Startable.class);
        for (int i = 0; i < startables.size(); i++) {
            ((Startable) startables.get(i)).start();
        }
    }

    public void dispose(PicoContainer node) {
        List startables = node.getComponentInstancesOfType(Startable.class);
        for (int i = 0; i < startables.size(); i++) {
            ((Startable) startables.get(i)).start();
        }
    }

}
