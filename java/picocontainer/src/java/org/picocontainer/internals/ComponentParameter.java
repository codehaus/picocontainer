package org.picocontainer.internals;

import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import java.io.Serializable;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class ComponentParameter implements Parameter, Serializable {

    private Object componentKey;

    public ComponentParameter(Object componentKey) {
        this.componentKey = componentKey;
    }

    public ComponentAdapter resolveAdapter(ComponentRegistry componentRegistry) throws AmbiguousComponentResolutionException {
        return componentRegistry.findComponentAdapter(componentKey);
    }
}
