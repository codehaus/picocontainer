/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.picoextras.script;

import junit.framework.TestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

public abstract class AbstractScriptedAssemblingLifecycleContainerBuilderTestCase extends TestCase {
    private ObjectReference containerRef = new SimpleReference();
    private ObjectReference parentContainerRef = new SimpleReference();

    protected PicoContainer buildContainer(ScriptedAssemblingLifecycleContainerBuilder builder) {
        builder.buildContainer(containerRef, parentContainerRef, "SOME_SCOPE");
        return (PicoContainer) containerRef.get();
    }
}