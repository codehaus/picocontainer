package picocontainer.defaults;

import picocontainer.PicoContainer;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoIntrospectionException;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public class ComponentParameter implements Parameter {
    public Object resolve(PicoContainer picoContainer, ComponentSpecification compSpec, Class requestedType)
            throws PicoInstantiationException, PicoIntrospectionException {
        // TODO figure out a way to remove this ugly cast?
        DefaultPicoContainer defaultPicoContainer = (DefaultPicoContainer) picoContainer;

        Object value = defaultPicoContainer.createComponent(requestedType);

        Object componentInstance = picoContainer.getComponent(requestedType);

        if (componentInstance != null) {
            return componentInstance;
        }

        componentInstance = defaultPicoContainer.findImplementingComponent(requestedType);

        if (componentInstance != null) {
            return componentInstance;
        }

        // try to find components that satisfy the interface (implements the component service asked for)
        ComponentSpecification componentSpecification =
                defaultPicoContainer.findImplementingComponentSpecification(requestedType);
        if (componentSpecification == null) {
            throw new UnsatisfiedDependencyInstantiationException(compSpec.getComponentImplementation(), requestedType);
        }

        requestedType = componentSpecification.getComponentType();

        // if the component does not exist yet, instantiate it lazily
        componentInstance = defaultPicoContainer.createComponent(componentSpecification);

        return value;
    }
}
