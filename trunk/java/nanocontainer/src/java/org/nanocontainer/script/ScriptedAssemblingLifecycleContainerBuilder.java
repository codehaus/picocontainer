/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.picoextras.script;

import org.picoextras.integrationkit.AssemblingLifecycleContainerBuilder;

import java.io.Reader;

public class ScriptedAssemblingLifecycleContainerBuilder extends AssemblingLifecycleContainerBuilder {
    protected final Reader script;
    protected final ClassLoader classLoader;

    public ScriptedAssemblingLifecycleContainerBuilder(Reader script, ClassLoader classLoader) {
        this.script = script;
        this.classLoader = classLoader;
    }
}