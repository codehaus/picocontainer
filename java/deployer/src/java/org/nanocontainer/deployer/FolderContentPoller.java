package org.nanocontainer.deployer;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.picocontainer.Startable;

/**
 * Component that polls a folder for children at regular intervals.
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class FolderContentPoller implements Startable {
    private FolderContentHandler folderContentHandler;
    private FileObject folder;

    private Runnable poller = new Runnable() {
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    // Have to "close" the folder to invalidate child cache
                    folder.close();
                    FileObject[] currentChildren = folder.getChildren();
                    folderContentHandler.setCurrentChildren(currentChildren);
                    synchronized(FolderContentPoller.this) {
                        FolderContentPoller.this.notify();
                        FolderContentPoller.this.wait(2000);
                    }
                } catch (FileSystemException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    thread.interrupt();
                }
            }
        }
    };
    private Thread thread;


    public FolderContentPoller(FolderContentHandler folderChangeNotifier) {
        this.folderContentHandler = folderChangeNotifier;
        folder = folderChangeNotifier.getFolder();
    }

    public void start() {
        thread = new Thread(poller);
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

}