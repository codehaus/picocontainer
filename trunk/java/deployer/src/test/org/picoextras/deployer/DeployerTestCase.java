package org.picoextras.deployer;

import junit.framework.TestCase;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.impl.VFSClassLoader;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

public class DeployerTestCase extends TestCase {

    public void testFolderWithDeploymentScriptAndClassesCanBeDeployed() throws FileSystemException, MalformedURLException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        DefaultFileSystemManager manager = new DefaultFileSystemManager();
        FileObject applicationFolder = getApplicationFolder(manager);

        Deployer deployer = new Deployer();
        ObjectReference containerRef = deployer.deploy(applicationFolder, manager, getClass().getClassLoader(), null);
        PicoContainer pico = (PicoContainer) containerRef.get();
        Object zap = pico.getComponentInstance("zap");
        assertEquals("Groovy Started", zap.toString());
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

    public void testZapClassCanBeLoadedByVFSClassLoader() throws FileSystemException, MalformedURLException, ClassNotFoundException {
        DefaultFileSystemManager manager = new DefaultFileSystemManager();
        FileObject applicationFolder = getApplicationFolder(manager);

        ClassLoader applicationClassLoader = new VFSClassLoader(applicationFolder, manager, getClass().getClassLoader());

        System.out.println("applicationFolder = " + applicationFolder.getName().getPath());
        System.out.println("classLoader = " + applicationClassLoader);
        Class c = applicationClassLoader.loadClass("foo.bar.Zap");
        System.out.println("c.getClassLoader() = " + c.getClassLoader());

    }

}