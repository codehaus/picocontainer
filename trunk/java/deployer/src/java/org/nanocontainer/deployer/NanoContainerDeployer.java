/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.deployer;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.impl.VFSClassLoader;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * This class is capable of deploying an application from any kind of file system
 * supported by <a href="http://jakarta.apache.org/commons/sandbox/vfs/">Jakarta VFS</a>.
 * (Like local files, zip files etc.) - following the NanoContainer scripting model.
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
public class NanoContainerDeployer implements Deployer {
    private final FileSystemManager fileSystemManager;

    public NanoContainerDeployer(FileSystemManager fileSystemManager) {
        this.fileSystemManager = fileSystemManager;
    }

    /**
     * Deploys an application.
     *
     * @param applicationFolder the root applicationFolder of the application.
     * @param parentClassLoader the classloader that loads the application classes.
     * @param parentContainerRef reference to the parent container (can be used to lookup components form a parent container).
     * @return an ObjectReference holding a PicoContainer with the deployed components
     * @throws org.apache.commons.vfs.FileSystemException if the file structure was bad.
     * @throws org.nanocontainer.integrationkit.PicoCompositionException if the deployment failed for some reason.
     */
    public ObjectReference deploy(FileObject applicationFolder, ClassLoader parentClassLoader, ObjectReference parentContainerRef) throws FileSystemException, ClassNotFoundException {
        ClassLoader applicationClassLoader = new VFSClassLoader(applicationFolder, fileSystemManager, parentClassLoader);

        FileObject deploymentScript = getDeploymentScript(applicationFolder);

        String extension = "." + deploymentScript.getName().getExtension();

        ObjectReference result = new SimpleReference();
        Reader scriptReader = new InputStreamReader(deploymentScript.getContent().getInputStream());

        NanoContainer nanoContainer = new NanoContainer(scriptReader, extension, applicationClassLoader);
        ContainerBuilder builder = nanoContainer.getContainerBuilder();
        builder.buildContainer(result, parentContainerRef, null);

        return result;
    }

    private FileObject getDeploymentScript(FileObject applicationFolder) throws FileSystemException {
        final FileObject metaInf = applicationFolder.getChild("META-INF");
        if(metaInf == null) {
            throw new FileSystemException("No deployment script (nanocontainer.[js|groovy|py|xml]) in " + applicationFolder.getName().getPath() + "/META-INF");
        }
        final FileObject[] picocontainerScripts = metaInf.findFiles(new FileSelector(){
            public boolean includeFile(FileSelectInfo fileSelectInfo) throws Exception {
                return fileSelectInfo.getFile().getName().getBaseName().startsWith("nanocontainer");
            }
            public boolean traverseDescendents(FileSelectInfo fileSelectInfo) throws Exception {
                return true;
            }
        });
        return picocontainerScripts[0];
    }
}