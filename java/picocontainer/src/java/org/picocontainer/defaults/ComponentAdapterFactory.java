package org.picocontainer.defaults;

import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ComponentAdapter;

/**
 * @author Jon Tirsen
 * @version $Revision$
 */
public interface ComponentAdapterFactory {
    ComponentAdapter createComponentAdapter(Object componentKey,
                                            Class componentImplementation,
                                            Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException;
}
