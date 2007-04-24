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

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Revision$
 */
public class PicoContainerBasedServiceManager implements ServiceManager {
    private final PicoContainer delegate;

    public PicoContainerBasedServiceManager(final PicoContainer delegate) {
        Check.argumentNotNull("delegate", delegate);
        this.delegate = delegate;
    }

    public Object lookup(final String s) throws ServiceException {
        try {
            Object result = delegate.getComponent(s);
            if( result == null && s != null )
            {
                try {
                    result = delegate.getComponent(Class.forName(s));
                } catch (ClassNotFoundException e) {
                    throw new ServiceException(s, "No component available by that key", e);
                }
            }
            return result;
        } catch (PicoException pe) {
            throw new ServiceException(s, "No component available by that key", pe);
        }
    }

    public boolean hasService(final String s) {
        return delegate.getComponentAdapter(s) != null;
    }

    public void release(final Object o) {
    }
}
