package org.nanocontainer.tools.deployer;

import junit.framework.TestCase;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.impl.VFSClassLoader;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;
import org.apache.commons.vfs.provider.zip.ZipFileProvider;
import org.nanocontainer.tools.deployer.Deployer;
import org.nanocontainer.tools.deployer.NanoContainerDeployer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NanoContainerDeployerTestCase extends TestCase {

    public void testFolderWithDeploymentScriptAndClassesCanBeDeployed() throws FileSystemException, MalformedURLException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        DefaultFileSystemManager manager = new DefaultFileSystemManager();
        FileObject applicationFolder = getApplicationFolder(manager);

        Deployer deployer = new NanoContainerDeployer(manager);
        ObjectReference containerRef = deployer.deploy(applicationFolder, getClass().getClassLoader(), null);
        PicoContainer pico = (PicoContainer) containerRef.get();
        Object zap = pico.getComponentInstance("zap");
        assertEquals("Groovy Started", zap.toString());
    }

    public void testZipWithDeploymentScriptAndClassesCanBeDeployed() throws FileSystemException, MalformedURLException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        DefaultFileSystemManager manager = new DefaultFileSystemManager();
        FileObject applicationFolder = getApplicationArchive(manager);

        Deployer deployer = new NanoContainerDeployer(manager);
        ObjectReference containerRef = deployer.deploy(applicationFolder, getClass().getClassLoader(), null);
        PicoContainer pico = (PicoContainer) containerRef.get();
        Object zap = pico.getComponentInstance("zap");
        assertEquals("Groovy Started", zap.toString());
    }

    public void testZapClassCanBeLoadedByVFSClassLoader() throws FileSystemException, MalformedURLException, ClassNotFoundException {
        DefaultFileSystemManager manager = new DefaultFileSystemManager();
        FileObject applicationFolder = getApplicationFolder(manager);
        ClassLoader applicationClassLoader = new VFSClassLoader(applicationFolder, manager, getClass().getClassLoader());
        applicationClassLoader.loadClass("foo.bar.Zap");
    }

    private FileObject getApplicationFolder(DefaultFileSystemManager manager) throws FileSystemException, MalformedURLException {
        manager.setDefaultProvider(new DefaultLocalFileProvider());
        manager.init();
        File testapp = new File("src/deploytest");
        String url = testapp.toURL().toExternalForm();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        FileObject applicationFolder = manager.resolveFile(url);
        return applicationFolder;
    }

    private FileObject getApplicationArchive(DefaultFileSystemManager manager) throws FileSystemException {
        manager.addProvider("file", new DefaultLocalFileProvider());
        manager.addProvider("zip", new ZipFileProvider());
        manager.init();
        File src = new File("src");
        FileObject applicationFolder = manager.resolveFile("zip:/" + src.getAbsolutePath() + "/deploytest.jar");
        return applicationFolder;
    }
}