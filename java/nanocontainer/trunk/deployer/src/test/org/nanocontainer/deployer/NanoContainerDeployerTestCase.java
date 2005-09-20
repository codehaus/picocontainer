package org.nanocontainer.deployer;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.impl.VFSClassLoader;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;
import org.apache.commons.vfs.provider.zip.ZipFileProvider;
import org.nanocontainer.deployer.Deployer;
import org.nanocontainer.deployer.NanoContainerDeployer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import junit.framework.TestCase;

import java.net.MalformedURLException;
import java.lang.reflect.InvocationTargetException;
import java.io.File;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NanoContainerDeployerTestCase extends TestCase {

    private String jarsDir = "target/booter/apps";
    private String folderPath = "src/deploytest";

    public void testZipWithDeploymentScriptAndClassesCanBeDeployed() throws FileSystemException, MalformedURLException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        DefaultFileSystemManager manager = new DefaultFileSystemManager();
        FileObject applicationArchive = getApplicationArchive(manager, jarsDir + "/successful-deploy.jar");

        Deployer deployer = new NanoContainerDeployer(manager);
        ObjectReference containerRef = deployer.deploy(applicationArchive, getClass().getClassLoader(), null);
        PicoContainer pico = (PicoContainer) containerRef.get();
        Object zap = pico.getComponentInstance("zap");
        assertEquals("Groovy Started", zap.toString());
    }

    public void testZipWithBadScriptNameThrowsFileSystemException() throws ClassNotFoundException, FileSystemException {

      DefaultFileSystemManager manager = new DefaultFileSystemManager();
      FileObject applicationFolder = getApplicationArchive(manager,  jarsDir + "/badscript-deploy.jar");

      try {
        Deployer deployer = new NanoContainerDeployer(manager);
        ObjectReference containerRef= deployer.deploy(applicationFolder, getClass().getClassLoader(), null);
        fail("Deployment should have thrown FileSystemException for bad script file name.  Instead got:" + containerRef.toString() + " built.");
      }
      catch (FileSystemException ex) {
        //a-ok
      }
    }

    public void testMalformedDeployerArchiveThrowsFileSystemException() throws ClassNotFoundException, FileSystemException {
      DefaultFileSystemManager manager = new DefaultFileSystemManager();
      FileObject applicationFolder = getApplicationArchive(manager,  jarsDir + "/malformed-deploy.jar");

      try {
        Deployer deployer = new NanoContainerDeployer(manager);
        ObjectReference containerRef= deployer.deploy(applicationFolder, getClass().getClassLoader(), null);
        fail("Deployment should have thrown FileSystemException for badly formed archive. Instead got:" + containerRef.toString() + " built.");
      }
      catch (FileSystemException ex) {
        //a-ok
      }
    }

    public void testFolderWithDeploymentScriptAndClassesCanBeDeployed() throws FileSystemException, MalformedURLException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        DefaultFileSystemManager manager = new DefaultFileSystemManager();
        FileObject applicationFolder = getApplicationFolder(manager, folderPath);

        try {
            Deployer deployer = null;
            deployer = new NanoContainerDeployer(manager);
            ObjectReference containerRef = deployer.deploy(applicationFolder, getClass().getClassLoader(), null);
            PicoContainer pico = (PicoContainer) containerRef.get();
            Object zap = pico.getComponentInstance("zap");
            assertEquals("Groovy Started", zap.toString());

        } catch (Exception e) {
            e.printStackTrace();  
        }
    }    

    public void testZapClassCanBeLoadedByVFSClassLoader() throws FileSystemException, MalformedURLException, ClassNotFoundException {
        DefaultFileSystemManager manager = new DefaultFileSystemManager();
        FileObject applicationFolder = getApplicationFolder(manager, folderPath);
        ClassLoader applicationClassLoader = new VFSClassLoader(applicationFolder, manager, getClass().getClassLoader());
        applicationClassLoader.loadClass("foo.bar.Zap");
    }

    public void testSettingDifferentBaseNameWillResultInChangeForWhatBuilderLooksFor() throws FileSystemException, MalformedURLException, ClassNotFoundException {
        DefaultFileSystemManager manager = new DefaultFileSystemManager();
        FileObject applicationFolder = getApplicationFolder(manager, folderPath);
        NanoContainerDeployer deployer = new NanoContainerDeployer(manager);
        assertEquals("nanocontainer", deployer.getFileBasename());

        deployer = new NanoContainerDeployer(manager,"foo");
        assertEquals("foo", deployer.getFileBasename());

        try {
            ObjectReference containerRef = deployer.deploy(applicationFolder, getClass().getClassLoader(), null);
            fail("Deployer should have now thrown an exception after changing the base name. Instead got: " + containerRef.toString());
        }
        catch (FileSystemException ex) {
            //a-ok
        }

    }

    private FileObject getApplicationFolder(final DefaultFileSystemManager manager, String folderPath) throws FileSystemException, MalformedURLException {
        manager.setDefaultProvider(new DefaultLocalFileProvider());
        manager.init();
        File testapp = new File(folderPath);
        String url = testapp.toURL().toExternalForm();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        FileObject applicationFolder = manager.resolveFile(url);
        return applicationFolder;
    }

    private FileObject getApplicationArchive(final DefaultFileSystemManager manager, final String jarPath) throws FileSystemException {
        manager.addProvider("file", new DefaultLocalFileProvider());
        manager.addProvider("zip", new ZipFileProvider());
        manager.init();
        File src = new File(jarPath);
        FileObject applicationFolder = manager.resolveFile("zip:/" + src.getAbsolutePath());
        return applicationFolder;
    }

}
