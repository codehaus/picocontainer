/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.picoextras.deployer;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.impl.VFSClassLoader;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.nanocontainer.script.jython.JythonContainerBuilder;
import org.nanocontainer.script.rhino.JavascriptContainerBuilder;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 *    | xxx               |  <-- parent app loader (must not contain classes from app builder classloader)
 *    +-------------------+
 *              |
 *    +-------------------+
 *    | someapp           | <-- app classloader (must not contain classes from app builder classloader)
 *    +-------------------+
 *              |
 *    +-------------------+
 *    | picocontainer     |
 *    | nanocontainer     |  <-- app builder classloader
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

    static {
        EXTENSION_TO_BUILDER_CLASS_MAP.put("groovy", GroovyContainerBuilder.class);
        EXTENSION_TO_BUILDER_CLASS_MAP.put("js", JavascriptContainerBuilder.class);
        EXTENSION_TO_BUILDER_CLASS_MAP.put("py", JythonContainerBuilder.class);
        EXTENSION_TO_BUILDER_CLASS_MAP.put("xml", XMLContainerBuilder.class);
    }

    private static Class getContainerBuilderClass(String extension) {
        return (Class) EXTENSION_TO_BUILDER_CLASS_MAP.get(extension);
    }

    /**
     * Deploys an application.
     *
     * @param applicationFolder the root applicationFolder of the application.
     * @param parentClassLoader the classloader that loads the application classes.
     * @param parentContainerRef reference to the parent container (can be used to lookup components form a parent container).
     * @return an ObjectReference holding a PicoContainer with the deployed components
     * @throws org.apache.commons.vfs.FileSystemException if the file structure was bad.
     * @throws org.nanocontainer.integrationkit.PicoAssemblyException if the deployment failed for some reason.
     */
    public ObjectReference deploy(FileObject applicationFolder, FileSystemManager fileSystemManager, ClassLoader parentClassLoader, ObjectReference parentContainerRef) throws FileSystemException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ClassLoader applicationClassLoader = new VFSClassLoader(applicationFolder, fileSystemManager, parentClassLoader);

        FileObject deploymentScript = getDeploymentScript(applicationFolder);
        MutablePicoContainer builderFactory = new DefaultPicoContainer();

        String extension = deploymentScript.getName().getExtension();
        Class builderClass = getContainerBuilderClass(extension);
        builderFactory.registerComponentImplementation("builder", builderClass);
        Reader scriptReader = new InputStreamReader(deploymentScript.getContent().getInputStream());
        builderFactory.registerComponentInstance(scriptReader);
        builderFactory.registerComponentInstance(applicationClassLoader);

        ContainerBuilder builder = (ContainerBuilder) builderFactory.getComponentInstance("builder");
        return invokeBuildContainer(builder, parentContainerRef);
    }

    private ObjectReference invokeBuildContainer(ContainerBuilder builder, ObjectReference parentContainerRef) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ObjectReference result = new SimpleReference();
        Method buildContainer = builder.getClass().getMethod("buildContainer", new Class[] {
            ObjectReference.class,
            ObjectReference.class,
            Object.class
        });
        buildContainer.invoke(builder, new Object[]{result, parentContainerRef, null});
        return result;
    }

    private FileObject getDeploymentScript(FileObject applicationFolder) throws FileSystemException {
        final FileObject metaInf = applicationFolder.getChild("META-INF");
        if(metaInf == null) {
            throw new FileSystemException("No deployment script (picocontainer.[js|groovy|py|xml]) in " + applicationFolder.getName().getPath() + "/META-INF");
        }
        final FileObject[] picocontainerScripts = metaInf.findFiles(new FileSelector(){
            public boolean includeFile(FileSelectInfo fileSelectInfo) throws Exception {
                return fileSelectInfo.getFile().getName().getBaseName().startsWith("picocontainer");
            }
            public boolean traverseDescendents(FileSelectInfo fileSelectInfo) throws Exception {
                return true;
            }
        });
        return picocontainerScripts[0];
    }
}