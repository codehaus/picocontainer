/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.script.bsh;

/**
 * @author <a href="mail at leosimons dot com">Leo Simons</a>
 * @author Aslak Hellesoy
 * @version $Id$
 */
public class ScriptableDemoBean {
    public Object key;
    public Object whatever;

    public void rememberMyKey(Object key) {
        this.key = key;
    }

    public void setWhatever(Object whatever) {
        this.whatever = whatever;
    }
}
