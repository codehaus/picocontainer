package org.picocontainer.defaults;

import org.picocontainer.LifecycleManager;
import org.picocontainer.MutablePicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface LifecycleManagerFactory {
    LifecycleManager createLifecycleManager(MutablePicoContainer pico);
}
