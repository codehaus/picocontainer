/*****************************************************************************
 * Copyright (C) MicroContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.microcontainer;

import java.io.File;
import java.net.URL;

/**
 * @author Paul Hammant
 * @version $Revision$
 */

public interface Kernel {
    void deploy(File marFile);

    void deploy(URL remoteMarFile); // aka lightweight java web start

    void deferredDeploy(File file);

    Object getComponent(String relativeComponentPath); // this concept of paths and nodes has no tests, so far the test could be using opaque strings

    void start(String startableNode); // this concept of paths and nodes has no tests, so far the test could be using opaque strings

    void stop(String startableNode); // this concept of paths and nodes has no tests, so far the test could be using opaque strings
}
