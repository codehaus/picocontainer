package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentAdapter;

import java.io.Serializable;

/**
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
