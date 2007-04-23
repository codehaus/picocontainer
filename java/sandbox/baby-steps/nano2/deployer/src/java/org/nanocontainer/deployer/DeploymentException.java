package org.nanocontainer.deployer;

import org.picocontainer.PicoException;

/**
 *
 * Runtime Wrapper Exception for errors in deployment.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DeploymentException extends PicoException {



    public DeploymentException(String message, Throwable t) {
        super(message,t);
    }

    public DeploymentException(Throwable t) {
        super(t);
    }
}
