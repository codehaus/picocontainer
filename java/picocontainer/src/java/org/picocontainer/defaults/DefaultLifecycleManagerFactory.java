package org.picocontainer.defaults;

import org.picocontainer.LifecycleManager;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.Disposable;

import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultLifecycleManagerFactory implements LifecycleManagerFactory, Serializable {
    public LifecycleManager createLifecycleManager(MutablePicoContainer pico) {
        return new DefaultLifecycleManager(pico, Startable.class, Startable.class, Disposable.class);
    }
}
