package org.picocontainer.adapters;

import org.picocontainer.ComponentFactory;

public interface BehaviorFactory extends ComponentFactory {
    ComponentFactory forThis(ComponentFactory delegate);
}
