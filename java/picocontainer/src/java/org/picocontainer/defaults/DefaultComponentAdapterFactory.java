package org.picocontainer.defaults;

import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;

import java.io.Serializable;

/**
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class DefaultComponentAdapterFactory implements ComponentAdapterFactory, Serializable {
    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new DefaultComponentAdapter(componentKey, componentImplementation, parameters);
    }
}
