/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.ioc.avalon;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Revision$
 */
public class PicoContainerBasedContext implements Context {
    private final PicoContainer delegate;

    public PicoContainerBasedContext(final PicoContainer delegate) {
        Check.argumentNotNull("delegate", delegate);
        this.delegate = delegate;
    }

    public Object get(final Object o) throws ContextException {
        try {
            return delegate.getComponent(o);
        } catch (PicoException pe) {
            throw new ContextException("No component available by that key", pe);
        }
    }
}
