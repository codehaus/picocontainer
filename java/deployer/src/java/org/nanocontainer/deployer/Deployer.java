package org.nanocontainer.deployer;

import org.picocontainer.defaults.ObjectReference;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface Deployer {
    ObjectReference deploy(FileObject applicationFolder, ClassLoader parentClassLoader, ObjectReference parentContainerRef) throws FileSystemException, ClassNotFoundException;
}