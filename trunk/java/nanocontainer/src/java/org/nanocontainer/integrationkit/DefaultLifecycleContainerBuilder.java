/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 23-Jan-2004
 * Time: 23:09:14
 */
package org.nanocontainer.integrationkit;

import org.picocontainer.MutablePicoContainer;

public class DefaultLifecycleContainerBuilder extends LifecycleContainerBuilder {
    private final ContainerComposer composer;

    public DefaultLifecycleContainerBuilder(ContainerComposer composer) {
        this.composer = composer;
    }

    protected void composeContainer(MutablePicoContainer container, Object assemblyScope) {
        composer.composeContainer(container, assemblyScope);
    }
}