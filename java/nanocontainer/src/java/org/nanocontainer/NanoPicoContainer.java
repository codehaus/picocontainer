package org.nanocontainer;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public interface NanoPicoContainer extends MutablePicoContainer, NanoContainer {

    MutablePicoContainer makeChildContainer(String name);

    void addChildContainer(String name, PicoContainer child);

}
