/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.hibernate;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

/**
 * implementors provide session session  management.
 *
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @version $Revision$
 */
public interface SessionProvider {

    /**
     * provide hibernate session out of factory create new one if necessary,
     *
     * @return The Session value
     * @throws HibernateException Description of Exception
     */
    Session getSession() throws HibernateException;


    /**
     * commit transaction currently underway, and start new one ( as side effect
     * hibernate session will be flushed )
     */
    void commit() throws HibernateException;

    /**
     * rollback active transaction if any was started. transaction will be reset
     *
     * @throws HibernateException if transaction can not be rolled back
     */
    void rollback() throws HibernateException;

    /**
     * normal session close.  commit transaction is any
     */
    void close() throws HibernateException;

    /**
     * reset and clean up everything. shall be used is something went
     * wrong ( for example you received hibernate exception )
     */
    void reset();

}
