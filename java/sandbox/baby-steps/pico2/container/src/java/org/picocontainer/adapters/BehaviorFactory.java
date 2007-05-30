package org.picocontainer.adapters;

import org.picocontainer.defaults.ComponentFactory;

public interface BehaviorFactory extends ComponentFactory {
    ComponentFactory forThis(ComponentFactory delegate);
}
