package org.nanocontainer.deployer;

import org.apache.commons.vfs.FileObject;

public interface FolderListener {
    void folderAdded(FileObject folder);
    void folderRemoved(FileObject fileObject);
}