package org.nanocontainer.deployer;

import junit.framework.TestCase;
import org.apache.commons.vfs.FileObject;
import org.jmock.C;
import org.jmock.Mock;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class FolderContentPollerTestCase extends TestCase {

    public void testShouldPollForNewFoldersAtRegularIntervals() throws InterruptedException {
        Mock rootFolderMock = new Mock(FileObject.class, "rootFolder");
        FileObject[] noChildren = new FileObject[0];
        rootFolderMock.expect("close");
        rootFolderMock.expectAndReturn("getChildren", noChildren);

        // Adding a child that will be returned at the second invocation of getChildren
        Mock newChildFolderMock = new Mock(FileObject.class, "childFolder");
        FileObject[] newChildren = new FileObject[] {(FileObject) newChildFolderMock.proxy()};

        FileObject rootFolder = (FileObject) rootFolderMock.proxy();

        Mock folderContentHandlerMock = new Mock(FolderContentHandler.class, "folderContentHandlerMock");
        folderContentHandlerMock.expectAndReturn("getFolder", rootFolder);
        folderContentHandlerMock.expect("setCurrentChildren", C.args(C.same(noChildren)));

        FolderContentPoller fileMonitor = new FolderContentPoller((FolderContentHandler) folderContentHandlerMock.proxy());

        fileMonitor.start();
        Thread.sleep(500);

        rootFolderMock.expect("close");
        rootFolderMock.expectAndReturn("getChildren", newChildren);
        folderContentHandlerMock.expect("setCurrentChildren", C.args(C.same(newChildren)));

        synchronized(fileMonitor.getLock()) {
            fileMonitor.getLock().notify();
        }

        rootFolderMock.verify();
        newChildFolderMock.verify();
        folderContentHandlerMock.verify();
    }
}