package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.Parameter;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public class ComponentParameter implements Parameter {
    private Object componentKey;

    public ComponentParameter() {
    }

    public ComponentParameter(Object componentKey) {
        this.componentKey = componentKey;
    }

    public Object resolve(PicoContainer picoContainer, ComponentSpecification compSpec, Class requestedType)
            throws PicoInitializationException {
        // TODO figure out a way to remove this ugly cast?
        DefaultPicoContainer defaultPicoContainer = (DefaultPicoContainer) picoContainer;

        Object componentInstance;
        ComponentSpecification componentSpecification = null;

        if (componentKey != null) {

            // is specified dependency already instantiated?
            componentInstance = picoContainer.getComponent(componentKey);

            if (componentInstance == null) {
                // try to find the component based on the key
                componentSpecification = defaultPicoContainer.getComponentSpecification(componentKey);
            }

        } else {

            // try to find it directly using the requestType as the key
            componentInstance = picoContainer.getComponent(requestedType);

            if (componentInstance == null) {

                // is there already an instantiated instance that satisfies the dependency?
                componentInstance = defaultPicoContainer.findImplementingComponent(requestedType);


                if (componentInstance == null) {
                    // try to find components that satisfy the interface (implements the component service asked for)
                    componentSpecification = defaultPicoContainer.findImplementingComponentSpecification(requestedType);
                }
            }

        }

        if (componentSpecification != null) {
            // if the component does not exist yet, instantiate it
            componentInstance = defaultPicoContainer.createComponent(componentSpecification);
        }

        if (componentInstance == null) {
            throw new UnsatisfiedDependencyInstantiationException(compSpec.getComponentImplementation(), componentKey, requestedType);
        }

        return componentInstance;
    }
}
