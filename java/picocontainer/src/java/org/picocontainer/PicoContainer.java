package org.picocontainer;

import java.util.Collection;

/**
 * This is the core interface for PicoContainer. It only has accessor methods.
 * In order to register components in a PicoContainer, use a {@link MutablePicoContainer},
 * such as {@link org.picocontainer.defaults.DefaultPicoContainer}. The preferrable
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public interface PicoContainer {
    Object getComponentInstance(Object componentKey) throws PicoException;

    Collection getComponentInstances() throws PicoException;

    boolean hasComponent(Object componentKey);

    Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) throws PicoException;

    Object getComponentMulticaster() throws PicoException;

    Collection getComponentKeys();
}
