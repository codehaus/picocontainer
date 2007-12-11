package org.nanocontainer.deployer;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.picocontainer.defaults.ObjectReference;

/**
 * A deployer provides a method of loading some sort of &quot;archive&quot; with a soft-configuration
 * system.  The archive can be compressed zips, remote urls, or standard file folders.
 * <p>It uses Apache Commons VFS for a unified resource model, but the actual
 * format of the 'archive' will depend on the implementation of the deployer.
 * See {@link org.nanocontainer.deployer.NanoContainerDeployer} for the default file format used.</p>
 * <p>Typically, the archive is deployed in its own unique VFS-based classloader to
 * provide independence of these archives. For those following development
 * of the PicoContainer world, a deployer can be considered a bit of a mini-microcontainer.</p>
 * @author Aslak Helles&oslash;y
 * @author Michael Rimov
 * @version $Revision$
 */
public interface Deployer {


    /**
     * Deploys some sort of application folder.  As far as NanoContainer deployment
     * goes, there is a null assembly scope associated with this method.
     * @param applicationFolder FileObject the base class of the 'archive'.  By
     * archive, the format depends on the deployer instance, and it may even
     * apply to things such remote URLs.  Must use Apache VFS
     * @param parentClassLoader The parent classloader to attach this container to.
     * @param parentContainerRef ObjectReference the parent container object reference.
     * @return ObjectReference a new object reference that container the new
     * container.
     * @throws FileSystemException upon VFS-based errors.
     * @throws ClassNotFoundException upon class instantiation error while running
     * the composition script.
     * @deprecated Since NanoContainer 1.2  (3/15/06).  Use the version of this
     * method with an assembly scope instead and pass in a null argument instead.
     */
    ObjectReference deploy(FileObject applicationFolder, ClassLoader parentClassLoader, ObjectReference parentContainerRef) throws FileSystemException, ClassNotFoundException;


    /**
     * Deploys some sort of application folder.  As far as NanoContainer deployment
     * goes, there is a null assembly scope associated with this method, and
     * @param applicationFolder FileObject the base class of the 'archive'.  By
     * archive, the format depends on the deployer instance, and it may even
     * apply to things such remote URLs.  Must use Apache VFS
     * @param parentClassLoader The parent classloader to attach this container to.
     * @param parentContainerRef ObjectReference the parent container object reference.
     * @param assemblyScope the assembly scope to use.  This can be any object desired,
     * (null is allowed) and when coupled with things like NanoWAR, it allows conditional assembly
     * of different components in the script.
     * @return ObjectReference a new object reference that container the new
     * container.
     * @throws FileSystemException upon VFS-based errors.
     * @throws ClassNotFoundException upon class instantiation error while running
     * the composition script.
     */
    ObjectReference deploy(FileObject applicationFolder, ClassLoader parentClassLoader, ObjectReference parentContainerRef, Object assemblyScope) throws FileSystemException, ClassNotFoundException;
}
