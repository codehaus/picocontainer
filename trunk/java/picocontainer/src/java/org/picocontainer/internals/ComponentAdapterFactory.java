package org.picocontainer.internals;

import org.picocontainer.PicoIntrospectionException;

/**
 * @author Jon Tirsen
 * @version $Revision$
 */
public interface ComponentAdapterFactory {
    ComponentAdapter createComponentAdapter(Object componentKey,
                                            Class componentImplementation,
                                            Parameter[] parameters) throws PicoIntrospectionException;
}
