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

import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cfg.Configuration;

/**
 * session factory provider, building one out of supplied configuration
 * ( strictly speaking this really trivial componen it overkill, but it's necessary
 * until hibernate folks provide a way to instantiate session factory via constructor 
 * ) 
 *
 * @author    Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @version   $Revision$
 */
public class DefaultSessionFactoryProvider implements SessionFactoryProvider {

    SessionFactory  sessionFactory;


    /**
     * create session factory provider from hibernate configuration
     *
     * @param configuration           hibernate configuration
     * @exception HibernateException  if session factory can not be built
     */
    public DefaultSessionFactoryProvider(Configuration configuration) throws HibernateException {
        sessionFactory = configuration.buildSessionFactory();
    }


    /**
     * session factory we have built
     * @return   The SessionFactory value
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
