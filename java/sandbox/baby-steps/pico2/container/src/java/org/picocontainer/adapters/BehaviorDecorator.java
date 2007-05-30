package org.picocontainer.adapters;

import org.picocontainer.defaults.ComponentFactory;

public interface BehaviorDecorator extends ComponentFactory {
    ComponentFactory forThis(ComponentFactory delegate);
}
