package org.nanocontainer.script;

import org.nanocontainer.NanoContainer;
import org.nanocontainer.DefaultNanoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.alternatives.EmptyPicoContainer;
import org.picocontainer.adapters.CachingComponentAdapterFactory;
import org.picocontainer.adapters.AnyInjectionComponentAdapterFactory;

public class ContainerElementHelper {
    public static NanoContainer makeNanoContainer(ComponentAdapterFactory caf, PicoContainer parent, ClassLoader classLoader) {
        if (parent == null) {
            parent = new EmptyPicoContainer();
        }
        if (caf == null) {
            caf = new CachingComponentAdapterFactory().forThis(new AnyInjectionComponentAdapterFactory());
        }
        return new DefaultNanoContainer(classLoader, new DefaultPicoContainer(caf, parent));

    }
}
