package org.picocontainer.defaults;

import org.picocontainer.Parameter;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 * @deprecated Use {@link ConstructorComponentAdapter}.
 */
public class TransientComponentAdapter extends ConstructorComponentAdapter {

    public TransientComponentAdapter(Object componentKey,
                                     Class componentImplementation) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation);
    }
}
