package org.nanocontainer.nanning;

import org.codehaus.nanning.config.AspectSystem;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.extras.DecoratingComponentAdapterFactory;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.ComponentAdapterFactory;
import org.picocontainer.internals.Parameter;

import java.io.Serializable;

/**
 * @author Jon Tirsen
 * @version $Revision$
 */
public class NanningComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    private AspectSystem aspectSystem;

    public NanningComponentAdapterFactory(AspectSystem aspectSystem,
                                          ComponentAdapterFactory delegate) {
        super(delegate);
        this.aspectSystem = aspectSystem;
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters)
            throws PicoIntrospectionException {
        return new NanningComponentAdapter(aspectSystem,
                super.createComponentAdapter(componentKey, componentImplementation, parameters));
    }
}
