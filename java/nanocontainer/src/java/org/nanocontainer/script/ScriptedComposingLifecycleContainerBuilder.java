/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.picoextras.script;

import org.picoextras.integrationkit.ComposingLifecycleContainerBuilder;

import java.io.Reader;

public class ScriptedComposingLifecycleContainerBuilder extends ComposingLifecycleContainerBuilder {
    protected final Reader script;
    protected final ClassLoader classLoader;

    public ScriptedComposingLifecycleContainerBuilder(Reader script, ClassLoader classLoader) {
        this.script = script;
        this.classLoader = classLoader;
    }
}