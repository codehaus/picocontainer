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

import java.sql.Connection;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import org.picocontainer.PicoInitializationException;
/** 
 * session delegator with failover behaviour in case of hibernate exception.
 * old session is disposed  and new one is obtained transparently. 
 * session creation is done lazily. 
 * 
 * @author Konstantin Pribluda
 * @version $Revision$ 
 */
public class FailoverSessionDelegator extends SessionDelegator {
    
    SessionFactory sessionFactory;
    Session session = null;
    
    public FailoverSessionDelegator(SessionFactory sessionFactory) {
        setSessionFactory(sessionFactory);
    }
    
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * obtain hibernate session in lazy way
     */ 
    public Session getSession()  {
        if(session == null) {
            try {
            session = sessionFactory.openSession();
            } catch(HibernateException ex) {
                throw new PicoInitializationException(ex);
            }
        }
        
        return session;
    }
    
    public Connection close()  throws HibernateException {
        Connection retval = null;
        try {
            retval = getSession().close();
        } catch(HibernateException ex) {
            session = null;
            throw ex;
        } finally {
            session = null;
        }
        
        return retval;
    }
    
    public void invalidateSession() throws HibernateException {
        if(session != null) {
            try {
                session.clear();
                session.close();
            } catch(HibernateException ex) {
                session = null;
                throw ex;
            } finally {
                session = null;
            }
        }
    }
}
