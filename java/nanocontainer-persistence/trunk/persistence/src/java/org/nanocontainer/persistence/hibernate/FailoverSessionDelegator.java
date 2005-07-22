/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.nanocontainer.persistence.hibernate;

import java.sql.Connection;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Session delegator with failover behaviour in case of hibernate exception. Old session is disposed
 * and new one is obtained transparently. Session creation is done lazily.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision: 2043 $
 */
public class FailoverSessionDelegator extends SessionDelegator {

    private SessionFactory sessionFactory;
    private Session session = null;
    private Interceptor interceptor = null;
    
	/**
	 * @param sessionFactory session factory to obtain session from 
	 */
    public FailoverSessionDelegator(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	/**
	 * @param sessionFactory session factory to obtain session from 
	 * @param exceptionHandler Exception handler component to use with created session
	 */
    public FailoverSessionDelegator(SessionFactory sessionFactory, HibernateExceptionHandler exceptionHandler) {
    	super(exceptionHandler);
        this.sessionFactory = sessionFactory;
    }

	/**
	 * @param sessionFactory sessionf actory to obtain session from
	 * @param interpceptor interceptor to use with created session
	 */
    public FailoverSessionDelegator(SessionFactory sessionFactory, Interceptor interceptor) {
    	this(sessionFactory);
    	setInterceptor(interceptor);
    }

	/**
	 * @param sessionFactory sessionf actory to obtain session from
	 * @param interpceptor interceptor to use with created session
	 * @param exceptionHandler Exception handler component to use with created session
	 */
    public FailoverSessionDelegator(SessionFactory sessionFactory, Interceptor interceptor, HibernateExceptionHandler exceptionHandler) {
    	this(sessionFactory, exceptionHandler);
    	setInterceptor(interceptor);
    }
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Obtain hibernate session in lazy way.
     */
    public Session getDelegatedSession() {
        if (session == null) {
            try {
            	session = interceptor == null ? sessionFactory.openSession() : sessionFactory.openSession(interceptor);
            } catch (RuntimeException ex) {
                throw handleException(ex);
            }
        }

        return session;
    }

    public Connection close() throws HibernateException {
        try {
            return getDelegatedSession().close();
        } catch (HibernateException ex) {
            session = null;
            throw handleException(ex);
        } finally {
            session = null;
        }
    }

    public void invalidateDelegatedSession() throws HibernateException {
        if (this.session != null) {
            try {
                session.clear();
                session.close();
            } catch (HibernateException ex) {
                session = null;
                throw handleException(ex);
            } finally {
                session = null;
            }
        }
    }

	public Interceptor getInterceptor() {
		return interceptor;
	}

	public void setInterceptor(Interceptor interceptor) {
		this.interceptor = interceptor;
	}

}
