package org.nanocontainer;

import org.nanocontainer.reflection.ReflectionContainerAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public interface SoftCompositionPicoContainer extends MutablePicoContainer, ReflectionContainerAdapter {

    MutablePicoContainer makeChildContainer(String name);
    void addChildContainer(String name, PicoContainer child);

}
