package org.nanocontainer.script.groovy;

import org.nanocontainer.NanoContainerBuilderDecorationDelegate;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.MutablePicoContainer;

import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 */
public class NullNanoContainerBuilderDecorationDelegate implements NanoContainerBuilderDecorationDelegate {
    public ComponentAdapterFactory decorate(ComponentAdapterFactory componentAdapterFactory, Map attributes) {
        return componentAdapterFactory;
    }

    public MutablePicoContainer decorate(MutablePicoContainer picoContainer) {
        return picoContainer;
    }

    public Object createChildOfContainerNode(Map attributes, Object name) {
        throw new NanoContainerBuilderException("Don't know how to create a '" + name + "' child of a container element");
    }

    public void rememberComponentKey(Map attributes) {
    }
}
