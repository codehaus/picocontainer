package org.nanocontainer.deployer;

import junit.framework.TestCase;
import org.jmock.Mock;
import org.jmock.C;
import org.apache.commons.vfs.FileObject;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DeployingFolderListenerTestCase extends TestCase {
    public void testFolderAddedShouldDeployApplication() {
        Mock folderMock = new Mock(FileObject.class);
        FileObject folder = (FileObject) folderMock.proxy();

        Mock deployerMock = new Mock(Deployer.class);
        deployerMock.expectAndReturn("deploy", C.args(
                C.same(folder),
                C.isA(ClassLoader.class),
                C.IS_ANYTHING
        ), null);
        Deployer deployer = (Deployer)deployerMock.proxy();
        DifferenceAnalysingFolderContentHandler handler = null;
        DeployingFolderListener deployingFolderListener = new DeployingFolderListener(deployer, handler);

        deployingFolderListener.folderAdded(folder);

        deployerMock.verify();
    }

}