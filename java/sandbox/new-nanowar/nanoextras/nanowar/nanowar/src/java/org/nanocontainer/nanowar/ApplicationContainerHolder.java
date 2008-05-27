package org.nanocontainer.nanowar;

import org.picocontainer.DefaultPicoContainer;

public class ApplicationContainerHolder {

    private final DefaultPicoContainer container;

    public ApplicationContainerHolder(DefaultPicoContainer container) {
        this.container = container;
    }

    DefaultPicoContainer getContainer() {
        return container;
    }
}
