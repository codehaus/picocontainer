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

import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.HibernateException;

import org.picocontainer.Startable;

/**
 * session provider with managed lifecycle. this class is in no way thread safe.
 * 
 * @author    Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @version   $Revision$
 */
public class LifecycleSessionProvider implements SessionProvider, Startable {

    Session         session = null;
    Transaction     transaction = null;
    SessionFactory  factory;

	/**
	 * create from session factory
	 */
	public LifecycleSessionProvider(SessionFactory factory) {
		this.factory = factory;
	}
	
    /**
     * create from provider
     */
    public LifecycleSessionProvider(SessionFactoryProvider sfp) {
         this.factory = sfp.getSessionFactory();
    }


    /**
     * get session instance for this request. if no session was created, create
     * new one.
     *
     * @return                        current or new hibernate session
     * @exception HibernateException  Description of Exception
     */
    public Session getSession() throws HibernateException {
        if (session == null) {
            session = getFactory().openSession();
			transaction = session.beginTransaction();
        }
        return session;
    }
	
    /**
	 * commit transaction currently underway, and start new one
     * @exception HibernateException  if transaction can not be 
	 *	commited for whatever reason
	 */
	public void commit() throws HibernateException {
		if(session != null && transaction != null &&  !transaction.wasCommitted()
			&& !transaction.wasRolledBack()) {
				transaction.commit();
				transaction = session.beginTransaction();
		}
	}

	
    /**
     * hibernate exception recovery method. revert all changes made to session,
     * rollback active transaction if any clear session content. after calling
     * session provider is in the state before session was obtained
     *
     * @exception HibernateException  Description of Exception
     */
    public void rollback() throws HibernateException {

        if (session != null && transaction != null && !transaction.wasCommitted() && !transaction.wasRolledBack()) {
            transaction.rollback();
			transaction = session.beginTransaction();
        }
    }


  

    /**
     * reset session if any
     */
    public void reset() {
        session = null;
        transaction = null;
    }


    /**
     * give session back and close it
     *
     * @param sess                    session to be closed
     * @exception HibernateException  may be thrown by hibernate
     */
    public void close() throws HibernateException {
		if(session != null) {
			if (transaction != null && transaction.wasCommitted() &&
            	!transaction.wasRolledBack()) {
					transaction.commit();
					transaction = null;
			}

			session.close();
			session = null;
		}
    }


    /**
     * basically a no op, since we prefer to be lazy
     */
    public void start() {
    }


    /**
     * stop container - close session normally. for now we just swallow any exception 
	 * which may happen here. we do not log from inside, and startable does not throw 
	 * anything... good news is that it does not matter at this point. 
     */
    public void stop() {
		try {
			close();
		} catch(HibernateException ex) {
			reset();
		}
    }


    /**
     * Gets the Factory attribute of the BaseSessionProvider object
     *
     * @return   The Factory value
     */
    SessionFactory getFactory() {
        return factory;
    }
}
