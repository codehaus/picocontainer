/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.defaults;

/**
 * A way to refer to objects that are stored in awkward places
 * (for example HttpSession or ThreadLocal).
 *
 * This is typically implemented by someone integrating Pico into
 * an existing container.
 *
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public interface ObjectReference {
    Object get();

    void set(Object item);
}
