package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ImplementationHidingComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    private final boolean strict;

    /**
     * For serialisation only. Do not use this constructor explicitly.
     */
    public ImplementationHidingComponentAdapterFactory() {
        this(null);
    }

    public ImplementationHidingComponentAdapterFactory(ComponentAdapterFactory delegate, boolean strict) {
        super(delegate);
        this.strict = strict;
    }

    public ImplementationHidingComponentAdapterFactory(ComponentAdapterFactory delegate) {
        this(delegate, true);
    }

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new ImplementationHidingComponentAdapter(super.createComponentAdapter(componentKey, componentImplementation, parameters), strict);
    }
}
