package org.nanocontainer.deployer;

import junit.framework.TestCase;
import org.jmock.Mock;
import org.jmock.C;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileType;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DifferenceAnalysingFolderContentHandlerTestCase extends TestCase {
    public void testAddedFoldersShouldCauseFolderAddedEvent() {
        Mock folderMock = new Mock(FileObject.class);
        DifferenceAnalysingFolderContentHandler handler = new DifferenceAnalysingFolderContentHandler((FileObject) folderMock.proxy(), null);

        Mock addedFolderMock = new Mock(FileObject.class);
        addedFolderMock.expectAndReturn("getType", FileType.FOLDER);
        FileObject addedFolder = (FileObject) addedFolderMock.proxy();

        Mock folderListenerMock = new Mock(FolderListener.class);
        folderListenerMock.expect("folderAdded", C.args(C.same(addedFolder)));
        handler.addFolderListener((FolderListener) folderListenerMock.proxy());

        handler.setCurrentChildren(new FileObject[]{addedFolder});

        folderMock.verify();
        addedFolderMock.verify();
        folderListenerMock.verify();
    }

    public void testRemovedFoldersShouldCauseFolderRemovedEvent() {
        Mock folderMock = new Mock(FileObject.class);
        DifferenceAnalysingFolderContentHandler handler = new DifferenceAnalysingFolderContentHandler((FileObject) folderMock.proxy(), null);

        Mock initialFolderOneMock = new Mock(FileObject.class);
        initialFolderOneMock.expectAndReturn("getType", FileType.FOLDER);
        FileObject initialFolderOne = (FileObject) initialFolderOneMock.proxy();
        Mock initialFolderTwoMock = new Mock(FileObject.class);
        initialFolderTwoMock.expectAndReturn("getType", FileType.FOLDER);
        initialFolderTwoMock.expectAndReturn("getType", FileType.FOLDER);
        FileObject initialFolderTwo = (FileObject) initialFolderTwoMock.proxy();
        FileObject[] initialFolders = new FileObject[] {initialFolderOne, initialFolderTwo};

        handler.setCurrentChildren(initialFolders);

        FileObject[] foldersAfterRemoval = new FileObject[] {initialFolderOne};
        Mock folderListenerMock = new Mock(FolderListener.class);
        folderListenerMock.expect("folderRemoved", C.args(C.same(initialFolderTwo)));
        handler.addFolderListener((FolderListener) folderListenerMock.proxy());

        handler.setCurrentChildren(foldersAfterRemoval);

        folderMock.verify();
        initialFolderOneMock.verify();
        initialFolderTwoMock.verify();
        folderListenerMock.verify();
    }
}