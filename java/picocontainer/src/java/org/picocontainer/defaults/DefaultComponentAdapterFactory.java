package org.picocontainer.defaults;

import org.picocontainer.internals.ComponentAdapterFactory;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.Parameter;
import org.picocontainer.PicoIntrospectionException;

import java.io.Serializable;

/**
 * @author Jon Tirsen
 * @version $Revision$
 */
public class DefaultComponentAdapterFactory implements ComponentAdapterFactory, Serializable {
    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters)
            throws PicoIntrospectionException {
        return new DefaultComponentAdapter(componentKey, componentImplementation, parameters);
    }
}
