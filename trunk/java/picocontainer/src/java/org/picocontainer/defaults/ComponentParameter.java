package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentAdapter;

import java.io.Serializable;

/**
 * A ComponentParameter should be used to pass in a particular component
 * as argument to a different component's constructor. This is particularly
 * useful in cases where several components of the same type have been registered,
 * but with a different key. Passing a ComponentParameter as a parameter
 * when registering a component will give PicoContainer a hint about what
 * other component to use in the constructor.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ComponentParameter implements Parameter, Serializable {

    private Object componentKey;

    public ComponentParameter(Object componentKey) {
        this.componentKey = componentKey;
    }

    public ComponentAdapter resolveAdapter(MutablePicoContainer picoContainer) throws PicoIntrospectionException {
        return picoContainer.findComponentAdapter(componentKey);
    }
}
