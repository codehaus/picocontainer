package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;

import java.io.Serializable;
import java.lang.reflect.Modifier;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public abstract class AbstractComponentAdapter implements ComponentAdapter, Serializable {
    private final Object componentKey;
    private final Class componentImplementation;

    protected AbstractComponentAdapter(Object componentKey, Class componentImplementation) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (componentImplementation == null) {
            throw new NullPointerException("componentImplementation");
        }
        this.componentKey = componentKey;
        this.componentImplementation = componentImplementation;
        checkTypeCompatibility();
        checkConcrete();
    }

    public Object getComponentKey() {
        if (componentKey == null) {
            throw new NullPointerException("componentKey");
        }
        return componentKey;
    }

    public Class getComponentImplementation() {
        return componentImplementation;
    }

    private void checkTypeCompatibility() throws AssignabilityRegistrationException {
        if (componentKey instanceof Class) {
            Class componentType = (Class) componentKey;
            if (!componentType.isAssignableFrom(componentImplementation)) {
                throw new AssignabilityRegistrationException(componentType, componentImplementation);
            }
        }
    }

    private void checkConcrete() throws NotConcreteRegistrationException {
        // Assert that the component class is concrete.
        boolean isAbstract = (componentImplementation.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT;
        if (componentImplementation.isInterface() || isAbstract) {
            throw new NotConcreteRegistrationException(componentImplementation);
        }
    }

}
