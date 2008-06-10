/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * license.html file.
 ******************************************************************************/
package org.picocontainer.persistence.hibernate;

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
import org.picocontainer.Startable;

/**
 * Session component with failover behaviour in case of hibernate exception. Old
 * session is disposed and new one is obtained transparently. Session creation
 * is done lazily.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 */
@SuppressWarnings("serial")
public final class SessionComponent implements Session, Startable {

    private Session session = null;

    private final SessionFactory sessionFactory;
    private final Interceptor interceptor;

    public SessionComponent(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        interceptor = null;
    }

    public SessionComponent(final SessionFactory sessionFactory, final Interceptor interceptor) {
        this.sessionFactory = sessionFactory;
        this.interceptor = interceptor;
    }

    /**
     * Obtain hibernate session.
     * 
     * @return
     * @param create
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
     * 
     * @return
     */
    protected final Session getDelegatedSession() {
        return getDelegatedSession(true);
    }

    /**
     * Perform actions to dispose "burned" session properly.
     */
    protected void invalidateDelegatedSession() {
        if (session != null) {
            try {
                session.clear();
                session.close();
            } catch (HibernateException ex) {
                throw handleException(ex);
            } finally {
                session = null;
            }
        }
    }

    /**
     * Invalidates the session calling {@link #invalidateDelegatedSession()} 
     * and return the <code>cause</code> back.
     * 
     * @return
     * @param cause
     */
    protected RuntimeException handleException(RuntimeException cause) {
        try {
            invalidateDelegatedSession();
        } catch (RuntimeException e) {
            return e;
        }
        return cause;
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
        close();
    }

    public Transaction beginTransaction() {
        try {
            return getDelegatedSession().beginTransaction();
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void cancelQuery() {
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

    public Connection close() {
        try {
            if (session == null) {
                // See Hibernate's javadoc. Returns Connection only when it only
                // was gave by application.
                return null;
            }

            return session.close();
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Connection connection() {
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

    public Query createFilter(Object collection, String queryString) {
        try {
            return getDelegatedSession().createFilter(collection, queryString);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Query createQuery(String queryString) {
        try {
            return getDelegatedSession().createQuery(queryString);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public SQLQuery createSQLQuery(String queryString) {
        try {
            return getDelegatedSession().createSQLQuery(queryString);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void delete(Object object) {
        try {
            getDelegatedSession().delete(object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void delete(String entityName, Object object) {
        try {
            getDelegatedSession().delete(entityName, object);
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

    public Connection disconnect() {
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

    public void evict(Object object) {
        try {
            getDelegatedSession().evict(object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void flush() {
        try {
            if (session == null) {
                return;
            }

            session.flush();
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Object get(Class clazz, Serializable id) {
        try {
            return getDelegatedSession().get(clazz, id);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Object get(Class clazz, Serializable id, LockMode lockMode) {
        try {
            return getDelegatedSession().get(clazz, id, lockMode);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Object get(String entityName, Serializable id) {
        try {
            return getDelegatedSession().get(entityName, id);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Object get(String entityName, Serializable id, LockMode lockMode) {
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

    public LockMode getCurrentLockMode(Object object) {
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

    public String getEntityName(Object object) {
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

    public Serializable getIdentifier(Object object) {
        try {
            return getDelegatedSession().getIdentifier(object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Query getNamedQuery(String queryName) {
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

    public Transaction getTransaction() {
        try {
            return getDelegatedSession().getTransaction();
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

    public boolean isDirty() {
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

    public Object load(Class theClass, Serializable id) {
        try {
            return getDelegatedSession().load(theClass, id);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Object load(Class theClass, Serializable id, LockMode lockMode) {
        try {
            return getDelegatedSession().load(theClass, id, lockMode);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void load(Object object, Serializable id) {
        try {
            getDelegatedSession().load(object, id);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Object load(String entityName, Serializable id) {
        try {
            return getDelegatedSession().load(entityName, id);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Object load(String entityName, Serializable id, LockMode lockMode) {
        try {
            return getDelegatedSession().load(entityName, id, lockMode);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void lock(Object object, LockMode lockMode) {
        try {
            getDelegatedSession().lock(object, lockMode);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void lock(String entityEntity, Object object, LockMode lockMode) {
        try {
            getDelegatedSession().lock(entityEntity, object, lockMode);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Object merge(Object object) {
        try {
            return getDelegatedSession().merge(object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Object merge(String entityName, Object object) {
        try {
            return getDelegatedSession().merge(entityName, object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void persist(Object object) {
        try {
            getDelegatedSession().persist(object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void persist(String entityName, Object object) {
        try {
            getDelegatedSession().persist(entityName, object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void reconnect() {
        try {
            getDelegatedSession().reconnect();
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void reconnect(Connection conn) {
        try {
            getDelegatedSession().reconnect(conn);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void refresh(Object object) {
        try {
            getDelegatedSession().refresh(object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void refresh(Object object, LockMode lockMode) {
        try {
            getDelegatedSession().refresh(object, lockMode);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void replicate(Object object, ReplicationMode replicationMode) {
        try {
            getDelegatedSession().replicate(object, replicationMode);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void replicate(String entityName, Object object, ReplicationMode replicationMode) {
        try {
            getDelegatedSession().replicate(entityName, object, replicationMode);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Serializable save(Object object) {
        try {
            return getDelegatedSession().save(object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public Serializable save(String entityName, Object object) {
        try {
            return getDelegatedSession().save(entityName, object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void saveOrUpdate(Object object) {
        try {
            getDelegatedSession().saveOrUpdate(object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void saveOrUpdate(String entityName, Object object) {
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

    public void setReadOnly(Object entity, boolean readOnly) {
        try {
            getDelegatedSession().setReadOnly(entity, readOnly);
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

    public void update(Object object) {
        try {
            getDelegatedSession().update(object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

    public void update(String entityName, Object object) {
        try {
            getDelegatedSession().update(entityName, object);
        } catch (RuntimeException ex) {
            throw handleException(ex);
        }
    }

}
