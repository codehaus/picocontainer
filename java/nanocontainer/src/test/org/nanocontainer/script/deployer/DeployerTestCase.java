package org.picoextras.script.deployer;

import junit.framework.TestCase;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;
import org.picocontainer.PicoContainer;
import org.picoextras.testmodel.WebServer;

import java.io.File;
import java.net.MalformedURLException;

public class DeployerTestCase extends TestCase {
    public void testFolderWithDeploymentScriptAndClassesCanBeDeployed() throws FileSystemException, MalformedURLException, ClassNotFoundException {
        Deployer deployer = new Deployer();
        DefaultFileSystemManager manager = new DefaultFileSystemManager();
        manager.setDefaultProvider(new DefaultLocalFileProvider());
        manager.init();
        File testapp = new File(System.getProperty("picoextras.script.home"), "src/deploytest");
        String url = testapp.toURL().toExternalForm();
        if(url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        FileObject rootFolder = manager.resolveFile(url);

        PicoContainer pico = deployer.deploy(rootFolder, manager, getClass().getClassLoader(), null);

        assertNotNull(pico.getComponentInstanceOfType(WebServer.class));
    }
}