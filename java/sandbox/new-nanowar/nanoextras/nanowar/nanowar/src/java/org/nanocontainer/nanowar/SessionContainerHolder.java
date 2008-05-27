package org.nanocontainer.nanowar;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.behaviors.Storing;

public class SessionContainerHolder {

    private final DefaultPicoContainer container;
    private final Storing storing;
    private final ThreadLocalLifecycleState lifecycleState;

    public SessionContainerHolder(DefaultPicoContainer container, Storing storing, ThreadLocalLifecycleState lifecycleState) {
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