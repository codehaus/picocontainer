/*****************************************************************************
 * Copyright (C) MegaContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.megacontainer.impl;

import org.megacontainer.Kernel;
import org.picocontainer.Startable;
import org.picocontainer.Disposable;

import java.io.File;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class DefaultKernel implements Kernel, Startable, Disposable {

    public void deploy(File marFile) {
    }

    public void deferredDeploy(File file) {
    }

    public Object getComponent(String relativeComponentPath) {
//        try {
//            Class tci = Class.forName("org.megacontainer.test.hopefullyhidden.TestCompImpl");
//            return tci.newInstance();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (InstantiationException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
        return new Object();
    }

    public void start(String startableNode) {
    }

    public void stop(String startableNode) {
    }

    // Start all containers.
    public void start() {
    }

    // Stop all containers.
    public void stop() {
    }

    // Dispose of all containers.
    public void dispose() {
    }
}
