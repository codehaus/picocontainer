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
    private final Object waitLock = new Object();

    private FolderContentHandler folderContentHandler;
    private FileObject folder;

    private Runnable poller = new Runnable() {
        public void run() {
            while (shouldRun) {
                try {
                    // Have to "close" the folder to invalidate child cache
                    folder.close();
                    FileObject[] currentChildren = folder.getChildren();
                    folderContentHandler.setCurrentChildren(currentChildren);
                    synchronized(waitLock) {
                        waitLock.wait(2000);
                    }
                } catch (FileSystemException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private Thread thread;
    private boolean shouldRun;


    public FolderContentPoller(FolderContentHandler folderChangeNotifier) {
        this.folderContentHandler = folderChangeNotifier;
        folder = folderChangeNotifier.getFolder();
    }

    public void start() {
        thread = new Thread(poller);
        shouldRun = true;
        thread.start();
    }

    public void stop() {
        shouldRun = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Only used from test to make it run faster
    Object getLock() {
        return waitLock;
    }

}