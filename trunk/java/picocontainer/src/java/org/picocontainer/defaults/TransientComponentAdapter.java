package org.picocontainer.defaults;

import org.picocontainer.Parameter;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 * @deprecated Use {@link ConstructorComponentAdapter}.
 */
public class TransientComponentAdapter extends ConstructorComponentAdapter {
    public TransientComponentAdapter(final Object componentKey,
                                     final Class componentImplementation,
                                     Parameter[] parameters) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters);
    }

    public TransientComponentAdapter(Object componentKey,
                                     Class componentImplementation) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation);
    }
}
