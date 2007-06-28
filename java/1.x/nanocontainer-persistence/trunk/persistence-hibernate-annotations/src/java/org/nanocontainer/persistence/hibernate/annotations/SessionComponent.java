/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.persistence.hibernate.annotations;

import java.io.Serializable;
import java.sql.Connection;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.SessionStatistics;
import org.nanocontainer.persistence.ExceptionHandler;
import org.picocontainer.Startable;

/**
 * Session component with failover behaviour in case of hibernate exception. Old session is disposed
 * and new one is obtained transparently. Session creation is done lazily.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Id: SessionComponent.java 2510 2005-09-22 10:11:19Z mauro $
 */
public class SessionComponent implements Session, Startable {

    private Session session = null;
    
	private SessionFactory sessionFactory;
    private Interceptor interceptor;
    protected ExceptionHandler hibernateExceptionHandler;

	public SessionComponent(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
		hibernateExceptionHandler = new PingPongExceptionHandler();
		interceptor = null;
	}

	public SessionComponent(final SessionFactory sessionFactory, final ExceptionHandler hibernateExceptionHandler) {
        this.sessionFactory = sessionFactory;
		this.hibernateExceptionHandler = hibernateExceptionHandler;
		interceptor = null;
	}

	public SessionComponent(final SessionFactory sessionFactory, final Interceptor interceptor) {
        this.sessionFactory = sessionFactory;
		hibernateExceptionHandler = new PingPongExceptionHandler();
		this.interceptor = interceptor;
	}

	public SessionComponent(final SessionFactory sessionFactory, final ExceptionHandler hibernateExceptionHandler, final Interceptor interceptor) {
        this.sessionFactory = sessionFactory;
		this.hibernateExceptionHandler = hibernateExceptionHandler;
		this.interceptor = interceptor;
	}

	/**
	 * Obtain hibernate session.  
	 */
	protected final Session getDelegatedSession(boolean create) {
        if (create && (session == null)) {
            try {
            	session = interceptor == null ? sessionFactory.openSession() : sessionFactory.openSession(interceptor);
            } catch (RuntimeException ex) {
                throw handleException(ex);
            }
        }

        return session;
	}

	/**
	 * Calls getDelegatedSession(true)
	 */
	protected final Session getDelegatedSession() {
        return getDelegatedSession(true);
	}

	/**
	 * Perform actions to dispose "burned" session properly.
	 */
	protected void invalidateDelegatedSession() throws HibernateException {
        if (session != null) {
            try {
                session.clear();
                session.close();           
            } finally {
                session = null;
            }
        }
	}

	/**
	 * Invalidates the session calling {@link #invalidateDelegatedSession()} and convert the <code>cause</code> using
	 * a {@link ExceptionHandler} if it's available otherwise just return the <code>cause</code> back.
	 */
	protected RuntimeException handleException(RuntimeException cause) throws HibernateException {
		try {
			invalidateDelegatedSession();
		} catch (RuntimeException e) {
			// Do nothing, only the original exception should be reported.
		}

		return hibernateExceptionHandler.handle(cause);
	}

	/**
	 * org.picocontainer.Startable.start()
	 */
	public void start() {
		// do nothing
	}

	/**
	 * org.picocontainer.Startable.stop()
	 */
	public void stop() {
		if (session != null && session.isOpen()) {
			close();
		}
	}

	public Transaction beginTransaction() throws HibernateException {
		try {
			return getDelegatedSession().beginTransaction();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void cancelQuery() throws HibernateException {
		try {
			getDelegatedSession().cancelQuery();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void clear() {
		try {
			if (session == null) {
				return;
			}

			session.clear();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Connection close() throws HibernateException {
		try {
			if (session == null) {
				// See Hibernate's javadoc. Returns Connection only when it only was gave by application.
				return null;
			}

			return session.close();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Connection connection() throws HibernateException {
		try {
			return getDelegatedSession().connection();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public boolean contains(Object object) {
		try {
			return getDelegatedSession().contains(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Criteria createCriteria(Class persistentClass) {
		try {
			return getDelegatedSession().createCriteria(persistentClass);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Criteria createCriteria(Class persistentClass, String alias) {
		try {
			return getDelegatedSession().createCriteria(persistentClass, alias);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Criteria createCriteria(String entityName) {
		try {
			return getDelegatedSession().createCriteria(entityName);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Criteria createCriteria(String entityName, String alias) {
		try {
			return getDelegatedSession().createCriteria(entityName, alias);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Query createFilter(Object collection, String queryString) throws HibernateException {
		try {
			return getDelegatedSession().createFilter(collection, queryString);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Query createQuery(String queryString) throws HibernateException {
		try {
			return getDelegatedSession().createQuery(queryString);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public SQLQuery createSQLQuery(String queryString) throws HibernateException {
		try {
			return getDelegatedSession().createSQLQuery(queryString);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void delete(Object object) throws HibernateException {
		try {
			getDelegatedSession().delete(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void disableFilter(String filterName) {
		try {
			getDelegatedSession().disableFilter(filterName);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Connection disconnect() throws HibernateException {
		try {
			return getDelegatedSession().disconnect();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Filter enableFilter(String filterName) {
		try {
			return getDelegatedSession().enableFilter(filterName);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void evict(Object object) throws HibernateException {
		try {
			getDelegatedSession().evict(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void flush() throws HibernateException {
		try {
			if (session == null) {
				return;
			}

			session.flush();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object get(Class clazz, Serializable id) throws HibernateException {
		try {
			return getDelegatedSession().get(clazz, id);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object get(Class clazz, Serializable id, LockMode lockMode) throws HibernateException {
		try {
			return getDelegatedSession().get(clazz, id, lockMode);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object get(String entityName, Serializable id) throws HibernateException {
		try {
			return getDelegatedSession().get(entityName, id);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
		try {
			return getDelegatedSession().get(entityName, id, lockMode);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public CacheMode getCacheMode() {
		try {
			return getDelegatedSession().getCacheMode();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public LockMode getCurrentLockMode(Object object) throws HibernateException {
		try {
			return getDelegatedSession().getCurrentLockMode(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Filter getEnabledFilter(String filterName) {
		try {
			return getDelegatedSession().getEnabledFilter(filterName);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public EntityMode getEntityMode() {
		try {
			return getDelegatedSession().getEntityMode();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public String getEntityName(Object object) throws HibernateException {
		try {
			return getDelegatedSession().getEntityName(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public FlushMode getFlushMode() {
		try {
			return getDelegatedSession().getFlushMode();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Serializable getIdentifier(Object object) throws HibernateException {
		try {
			return getDelegatedSession().getIdentifier(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Query getNamedQuery(String queryName) throws HibernateException {
		try {
			return getDelegatedSession().getNamedQuery(queryName);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Session getSession(EntityMode entityMode) {
		try {
			return getDelegatedSession().getSession(entityMode);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public SessionFactory getSessionFactory() {
		try {
			return getDelegatedSession().getSessionFactory();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public SessionStatistics getStatistics() {
		try {
			return getDelegatedSession().getStatistics();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public boolean isConnected() {
		try {
			return getDelegatedSession().isConnected();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public boolean isDirty() throws HibernateException {
		try {
			return getDelegatedSession().isDirty();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public boolean isOpen() {
		try {
			return getDelegatedSession().isOpen();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object load(Class theClass, Serializable id) throws HibernateException {
		try {
			return getDelegatedSession().load(theClass, id);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object load(Class theClass, Serializable id, LockMode lockMode) throws HibernateException {
		try {
			return getDelegatedSession().load(theClass, id, lockMode);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void load(Object object, Serializable id) throws HibernateException {
		try {
			getDelegatedSession().load(object, id);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object load(String entityName, Serializable id) throws HibernateException {
		try {
			return getDelegatedSession().load(entityName, id);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
		try {
			return getDelegatedSession().load(entityName, id, lockMode);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void lock(Object object, LockMode lockMode) throws HibernateException {
		try {
			getDelegatedSession().lock(object, lockMode);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void lock(String entityEntity, Object object, LockMode lockMode) throws HibernateException {
		try {
			getDelegatedSession().lock(entityEntity, object, lockMode);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object merge(Object object) throws HibernateException {
		try {
			return getDelegatedSession().merge(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object merge(String entityName, Object object) throws HibernateException {
		try {
			return getDelegatedSession().merge(entityName, object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void persist(Object object) throws HibernateException {
		try {
			getDelegatedSession().persist(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void persist(String entityName, Object object) throws HibernateException {
		try {
			getDelegatedSession().persist(entityName, object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void reconnect() throws HibernateException {
		try {
			getDelegatedSession().reconnect();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void reconnect(Connection conn) throws HibernateException {
		try {
			getDelegatedSession().reconnect(conn);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void refresh(Object object) throws HibernateException {
		try {
			getDelegatedSession().refresh(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void refresh(Object object, LockMode lockMode) throws HibernateException {
		try {
			getDelegatedSession().refresh(object, lockMode);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException {
		try {
			getDelegatedSession().replicate(object, replicationMode);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void replicate(String entityName, Object object, ReplicationMode replicationMode) throws HibernateException {
		try {
			getDelegatedSession().replicate(entityName, object, replicationMode);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Serializable save(Object object) throws HibernateException {
		try {
			return getDelegatedSession().save(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	

	public Serializable save(String entityName, Object object) throws HibernateException {
		try {
			return getDelegatedSession().save(entityName, object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}


	public void saveOrUpdate(Object object) throws HibernateException {
		try {
			getDelegatedSession().saveOrUpdate(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void saveOrUpdate(String entityName, Object object) throws HibernateException {
		try {
			getDelegatedSession().saveOrUpdate(entityName, object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void setCacheMode(CacheMode cacheMode) {
		try {
			getDelegatedSession().setCacheMode(cacheMode);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void setFlushMode(FlushMode value) {
		try {
			getDelegatedSession().setFlushMode(value);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void update(Object object) throws HibernateException {
		try {
			getDelegatedSession().update(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}



	public void update(String entityName, Object object) throws HibernateException {
		try {
			getDelegatedSession().update(entityName, object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}


	/**
	 * A not to do "if (handler == null)" ping-pong ExceptionHandler version.
	 */
	private class PingPongExceptionHandler implements ExceptionHandler {

		public RuntimeException handle(Throwable ex) {
			return (RuntimeException) ex;
		}

	}

    /**
     * {@inheritDoc}
     * @param entityName
     * @param object
     * @throws HibernateException
     * @see org.hibernate.Session#delete(java.lang.String, java.lang.Object)
     */
    public void delete(String entityName, Object object) throws HibernateException {
        getDelegatedSession().delete(entityName, object);
        
    }

    /**
     * {@inheritDoc}
     * @return Transaction.
     * @see org.hibernate.Session#getTransaction()
     */
    public Transaction getTransaction() {
        return getDelegatedSession().getTransaction();
    }

    /**
     * {@inheritDoc}
     * @param entity
     * @param readOnly
     * @see org.hibernate.Session#setReadOnly(java.lang.Object, boolean)
     */
    public void setReadOnly(Object entity, boolean readOnly) {
        getDelegatedSession().setReadOnly(entity, readOnly);
    }

}
