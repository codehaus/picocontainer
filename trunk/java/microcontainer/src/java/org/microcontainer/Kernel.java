/*****************************************************************************
 * Copyright (C) MegaContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.megacontainer;

import java.io.File;

/**
 * @author Paul Hammant
 * @version $Revision$
 */

public interface Kernel {
    void deploy(File marFile);

    void deferredDeploy(File file);

    Object getComponent(String relativeComponentPath);

    void start(String startableNode);

    void stop(String startableNode);
}
