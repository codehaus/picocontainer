/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 23-Jan-2004
 * Time: 23:34:27
 */
package org.picoextras.integrationkit;

import org.picocontainer.MutablePicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ComposingLifecycleContainerBuilder extends LifecycleContainerBuilder {
    protected void composeContainer(MutablePicoContainer container, Object assemblyScope) {
        // do nothing. assume that this is done in buildContainer().
    }
}