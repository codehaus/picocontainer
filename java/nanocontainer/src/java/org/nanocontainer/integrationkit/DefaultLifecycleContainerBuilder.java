/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 23-Jan-2004
 * Time: 23:09:14
 */
package org.picoextras.integrationkit;

import org.picocontainer.MutablePicoContainer;

public class DefaultLifecycleContainerBuilder extends LifecycleContainerBuilder {
    private final ContainerComposer assembler;

    public DefaultLifecycleContainerBuilder(ContainerComposer assembler) {
        this.assembler = assembler;
    }

    protected void composeContainer(MutablePicoContainer container, Object assemblyScope) {
        assembler.composeContainer(container, assemblyScope);
    }
}