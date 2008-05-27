package org.nanocontainer.nanowar;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.lifecycle.LifecycleState;
import org.picocontainer.behaviors.Storing;

public class RequestContainerHolder {

    private final DefaultPicoContainer container;
    private final Storing storing;
    private final ThreadLocalLifecycleState lifecycleState;

    public RequestContainerHolder(DefaultPicoContainer container, Storing storing, ThreadLocalLifecycleState lifecycleState) {
        this.container = container;
        this.storing = storing;
        this.lifecycleState = lifecycleState;
    }

    DefaultPicoContainer getContainer() {
        return container;
    }

    Storing getStoring() {
        return storing;
    }

    ThreadLocalLifecycleState getLifecycleStateModel() {
        return lifecycleState;
    }
}