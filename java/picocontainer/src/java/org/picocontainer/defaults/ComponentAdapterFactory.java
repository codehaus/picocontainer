package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;

/**
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public interface ComponentAdapterFactory {
    ComponentAdapter createComponentAdapter(Object componentKey,
                                            Class componentImplementation,
                                            Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException;
}
