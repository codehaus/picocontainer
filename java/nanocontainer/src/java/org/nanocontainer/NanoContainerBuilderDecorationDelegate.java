package org.nanocontainer;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;

import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface NanoContainerBuilderDecorationDelegate {

    ComponentAdapterFactory decorate(ComponentAdapterFactory componentAdapterFactory, Map attributes);

    MutablePicoContainer decorate(MutablePicoContainer picoContainer);

    Object createChildOfContainerNode(Map attributes, Object name);

    void rememberComponentKey(Map attributes);
}
