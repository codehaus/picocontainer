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

import org.picocontainer.PicoInitializationException;

/**
 * @author <a href="mail at leosimons dot com">Leo Simons</a>
 * @version $Id$
 */
public class BeanShellScriptInitializationException
        extends PicoInitializationException {

    BeanShellScriptInitializationException(String message) {
        super(message);
    }

    BeanShellScriptInitializationException(Throwable cause) {
        this("BeanShellScriptInitializationException: " + cause.getClass().getName() + " "
                + cause.getMessage());
    }
}
