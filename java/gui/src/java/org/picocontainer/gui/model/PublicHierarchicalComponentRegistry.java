package org.picocontainer.gui.model;

import org.picocontainer.extras.HierarchicalComponentRegistry;
import org.picocontainer.internals.ComponentRegistry;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PublicHierarchicalComponentRegistry extends HierarchicalComponentRegistry {
    public PublicHierarchicalComponentRegistry(ComponentRegistry parentRegistry, ComponentRegistry componentRegistry) {
        super(parentRegistry, componentRegistry);
    }
}
