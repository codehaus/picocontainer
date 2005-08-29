package org.nanocontainer.deployer;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.picocontainer.Startable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DeployingFolderListener implements FolderListener, Startable {
    private final Deployer deployer;
    private final DifferenceAnalysingFolderContentHandler handler;

    public DeployingFolderListener(Deployer deployer, DifferenceAnalysingFolderContentHandler handler) {
        this.deployer = deployer;
        this.handler = handler;
    }

    public void folderAdded(FileObject folder) {
        try {
            deployer.deploy(folder, getClass().getClassLoader(), null);
        } catch (FileSystemException e) {
            throw new DeploymentException(e);
        } catch (ClassNotFoundException e) {
            throw new DeploymentException(e);
        }
    }

    public void folderRemoved(FileObject fileObject) {

    }

    public void start() {
        handler.addFolderListener(this);
    }

    public void stop() {
        handler.removeFolderListener(this);
    }
}