/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.script.deployer;

import org.apache.commons.vfs.*;
import org.apache.commons.vfs.impl.VFSClassLoader;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.picoextras.integrationkit.PicoAssemblyException;
import org.nanocontainer.script.rhino.JavascriptContainerBuilder;
import org.nanocontainer.script.jython.JythonContainerBuilder;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.nanocontainer.script.ScriptedComposingLifecycleContainerBuilder;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is capable of deploying an application from any kind of file system
 * supported by <a href="http://jakarta.apache.org/commons/sandbox/vfs/">Jakarta VFS</a>.
 * (Like local files, zip files etc.)
 *
 * The root folder to deploy must have the following file structure:
 * <pre>
 * +-someapp/
 *   +-META-INF/
 *   | +-picocontainer.[py|js|xml]
 *   +-com/
 *     +-blablah/
 *       +-Hip.class
 *       +-Hop.class
 * </pre>
 *
 * For those familiar with J2EE containers (or other containers for that matter), the
 * META-INF/picocontainer script is the <em>deployment script</em>. It plays the same
 * role as more classical "deployment descriptors", except that deploying via a full blown
 * scripting language is a lot more powerful!
 *
 * A new class loader (which will be a child of parentClassLoader) will be created. This classloader will make
 * the classes under the root folder available to the deployment script.
 *
 * IMPORTANT NOTE:
 * The scripting engine (rhino, jython, groovy etc.) should be loaded by the same classLoader as
 * the appliacation classes, i.e. the VFSClassLoader pointing to the app directory.
 *
 * <pre>
 *    +-------------------+
 *    | xxx               |  <-- parent app loader
 *    +-------------------+
 *              |
 *    +-------------------+
 *    | someapp           | <-- app classloader
 *    +-------------------+
 *              |
 *    +-------------------+
 *    | picoextras-script |  <-- app builder classloader
 *    | rhino             |
 *    | jython            |
 *    | groovy            |
 *    +-------------------+
 * </pre>
 *
 * This means that these scripting engines should *not* be accessible by any of the app classloader, since this
 * may prevent the scripting engine from seeing the classes loaded by the VFSClassLoader. In other words,
 * the scripting engine classed may be loaded several times by different class loaders - once for each
 * deployed application.
 *
 * @author Aslak Helles&oslash;y
 */
public class Deployer {
    private static final Map EXTENSION_TO_BUILDER_CLASS_MAP = new HashMap();
    private final ObjectReference containerRef = new SimpleReference();

    static {
        EXTENSION_TO_BUILDER_CLASS_MAP.put("js", JavascriptContainerBuilder.class);
        EXTENSION_TO_BUILDER_CLASS_MAP.put("py", JythonContainerBuilder.class);
        EXTENSION_TO_BUILDER_CLASS_MAP.put("xml", XMLContainerBuilder.class);
    }

    /**
     * Deploys an application.
     *
     * @param folder the root folder of the application.
     * @param applicationClassLoader the classloader that loads the application classes.
     * @param parentContainerRef reference to the parent container (can be used to lookup components form a parent container).
     * @return a PicoContainer with the deployed components
     * @throws org.apache.commons.vfs.FileSystemException if the file structure was bad.
     * @throws org.picoextras.integrationkit.PicoAssemblyException if the deployment failed for some reason.
     */
    public PicoContainer deploy(FileObject folder, FileSystemManager fileSystemManager, ClassLoader applicationClassLoader, ObjectReference parentContainerRef) throws FileSystemException, PicoAssemblyException, ClassNotFoundException {
        final FileObject metaInf = folder.getChild("META-INF");
        final FileObject[] picocontainerScripts = metaInf.findFiles(new FileSelector(){
            public boolean includeFile(FileSelectInfo fileSelectInfo) throws Exception {
                return fileSelectInfo.getFile().getName().getBaseName().startsWith("picocontainer");
            }
            public boolean traverseDescendents(FileSelectInfo fileSelectInfo) throws Exception {
                return true;
            }
        });
        ClassLoader appClassLoader = new VFSClassLoader(folder, fileSystemManager, applicationClassLoader);

        String extension = picocontainerScripts[0].getName().getExtension();
        Class builderClass = (Class) EXTENSION_TO_BUILDER_CLASS_MAP.get(extension);

        MutablePicoContainer builderFactory = new DefaultPicoContainer();
        builderFactory.registerComponentImplementation(builderClass);
        builderFactory.registerComponentInstance(new InputStreamReader(picocontainerScripts[0].getContent().getInputStream()));
        builderFactory.registerComponentInstance(appClassLoader);

        ScriptedComposingLifecycleContainerBuilder builder = (ScriptedComposingLifecycleContainerBuilder) builderFactory.getComponentInstanceOfType(ScriptedComposingLifecycleContainerBuilder.class);

        builder.buildContainer(containerRef, parentContainerRef, null);

        return (PicoContainer) containerRef.get();
    }
}