/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 23-Jan-2004
 * Time: 23:34:27
 */
package org.picoextras.integrationkit;

import org.picocontainer.MutablePicoContainer;

/**
 * {@inheritDoc}
 * Implementations of this ContainerBuilder should also configure the
 * container (register components) in the {@link #buildContainer} method.
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class AssemblingLifecycleContainerBuilder extends LifecycleContainerBuilder {
    protected void assembleContainer(MutablePicoContainer container, Object assemblyScope) {
        // do nothing. assume that this is done in buildContainer().
    }
}