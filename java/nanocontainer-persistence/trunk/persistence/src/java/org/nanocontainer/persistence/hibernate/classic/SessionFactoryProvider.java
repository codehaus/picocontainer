/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.persistence.hibernate.classic;

import net.sf.hibernate.SessionFactory;

/**
 * contract for session factory builders
 *
 * @author    Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @version   $Revision$
 * @deprecated since we have SessionFactoryComponentAdapter
 */
public interface SessionFactoryProvider {

    /**
     * Gets the SessionFactory attribute of the SessionFactoryProvider object
     *
     * @return   The SessionFactory value
     */
    public SessionFactory getSessionFactory();
}

