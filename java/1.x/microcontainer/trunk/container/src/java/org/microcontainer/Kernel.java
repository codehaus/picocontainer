/*****************************************************************************
 * Copyright (C) MicroContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.microcontainer;

import org.picocontainer.PicoContainer;

import java.io.File;
import java.net.URL;

/**
 * @author Paul Hammant
 * @version $Revision$
 */

public interface Kernel {
    void deploy(File mcaFile) throws DeploymentException;

    void deploy(URL remoteMcaFile) throws DeploymentException; // aka lightweight java web start (context determined from mcaFile name)

	void deploy(String context, URL mcaFile) throws DeploymentException; // handleDeployForMcaFile the mcaFile to the named context

    void deferredDeploy(File file) throws DeploymentException ;

    Object getComponent(String relativeComponentPath); // this concept of paths and nodes has no tests, so far the test could be using opaque strings

    PicoContainer getRootContainer(String context);

	int size();

    void start(String startableNode); // this concept of paths and nodes has no tests, so far the test could be using opaque strings

    void stop(String startableNode); // this concept of paths and nodes has no tests, so far the test could be using opaque strings
}



