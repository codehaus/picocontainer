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

import java.io.Serializable;
import java.sql.Connection;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Abstract base class for session delegators. delegates all calls to session obtained by
 * implementing class. error handling is also there. All methods are just delegations to hibernate
 * session.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
 */
public abstract class SessionDelegator implements Session {

    /**
     * Obtain hibernate session.
     */
    public abstract Session getDelegatedSession();

    /**
     * Perform actions to dispose "burned" session properly.
     */
    public abstract void invalidateDelegatedSession() throws HibernateException;

    public Transaction beginTransaction() throws HibernateException {
        try {
            return this.getDelegatedSession().beginTransaction();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void cancelQuery() throws HibernateException {
        try {
            this.getDelegatedSession().cancelQuery();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void clear() {
        try {
            this.getDelegatedSession().clear();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Connection close() throws HibernateException {
        try {
            return this.getDelegatedSession().close();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Connection connection() throws HibernateException {
        try {
            return this.getDelegatedSession().connection();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public boolean contains(Object object) {
        try {
            return this.getDelegatedSession().contains(object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Criteria createCriteria(Class persistentClass) {
        try {
            return this.getDelegatedSession().createCriteria(persistentClass);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Criteria createCriteria(Class persistentClass, String alias) {
        try {
            return this.getDelegatedSession().createCriteria(persistentClass, alias);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Criteria createCriteria(String entityName) {
        try {
            return this.getDelegatedSession().createCriteria(entityName);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Criteria createCriteria(String entityName, String alias) {
        try {
            return this.getDelegatedSession().createCriteria(entityName, alias);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Query createFilter(Object collection, String queryString) throws HibernateException {
        try {
            return this.getDelegatedSession().createFilter(collection, queryString);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Query createQuery(String queryString) throws HibernateException {
        try {
            return this.getDelegatedSession().createQuery(queryString);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public SQLQuery createSQLQuery(String queryString) throws HibernateException {
        try {
            return this.getDelegatedSession().createSQLQuery(queryString);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void delete(Object object) throws HibernateException {
        try {
            this.getDelegatedSession().delete(object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void disableFilter(String filterName) {
        try {
            this.getDelegatedSession().disableFilter(filterName);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Connection disconnect() throws HibernateException {
        try {
            return this.getDelegatedSession().disconnect();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Filter enableFilter(String filterName) {
        try {
            return this.getDelegatedSession().enableFilter(filterName);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void evict(Object object) throws HibernateException {
        try {
            this.getDelegatedSession().evict(object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void flush() throws HibernateException {
        try {
            this.getDelegatedSession().flush();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Object get(Class clazz, Serializable id) throws HibernateException {
        try {
            return this.getDelegatedSession().get(clazz, id);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Object get(Class clazz, Serializable id, LockMode lockMode) throws HibernateException {
        try {
            return this.getDelegatedSession().get(clazz, id, lockMode);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Object get(String entityName, Serializable id) throws HibernateException {
        try {
            return this.getDelegatedSession().get(entityName, id);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
        try {
            return this.getDelegatedSession().get(entityName, id, lockMode);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public CacheMode getCacheMode() {
        try {
            return this.getDelegatedSession().getCacheMode();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public LockMode getCurrentLockMode(Object object) throws HibernateException {
        try {
            return this.getDelegatedSession().getCurrentLockMode(object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Filter getEnabledFilter(String filterName) {
        try {
            return this.getDelegatedSession().getEnabledFilter(filterName);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public EntityMode getEntityMode() {
        try {
            return this.getDelegatedSession().getEntityMode();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public String getEntityName(Object object) throws HibernateException {
        try {
            return this.getDelegatedSession().getEntityName(object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public FlushMode getFlushMode() {
        try {
            return this.getDelegatedSession().getFlushMode();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Serializable getIdentifier(Object object) throws HibernateException {
        try {
            return this.getDelegatedSession().getIdentifier(object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Query getNamedQuery(String queryName) throws HibernateException {
        try {
            return this.getDelegatedSession().getNamedQuery(queryName);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Session getSession(EntityMode entityMode) {
        try {
            return this.getDelegatedSession().getSession(entityMode);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public SessionFactory getSessionFactory() {
        try {
            return this.getDelegatedSession().getSessionFactory();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    /**
     * Handle an HibernateException. The default behavior throw cause again.
     */
    public RuntimeException handleException(HibernateException cause) throws HibernateException {
        throw cause;
    }

    public boolean isConnected() {
        try {
            return this.getDelegatedSession().isConnected();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public boolean isDirty() throws HibernateException {
        try {
            return this.getDelegatedSession().isDirty();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public boolean isOpen() {
        try {
            return this.getDelegatedSession().isOpen();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Object load(Class theClass, Serializable id) throws HibernateException {
        try {
            return this.getDelegatedSession().load(theClass, id);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Object load(Class theClass, Serializable id, LockMode lockMode) throws HibernateException {
        try {
            return this.getDelegatedSession().load(theClass, id, lockMode);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void load(Object object, Serializable id) throws HibernateException {
        try {
            this.getDelegatedSession().load(object, id);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Object load(String entityName, Serializable id) throws HibernateException {
        try {
            return this.getDelegatedSession().load(entityName, id);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
        try {
            return this.getDelegatedSession().load(entityName, id, lockMode);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void lock(Object object, LockMode lockMode) throws HibernateException {
        try {
            this.getDelegatedSession().lock(object, lockMode);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void lock(String entityEntity, Object object, LockMode lockMode) throws HibernateException {
        try {
            this.getDelegatedSession().lock(entityEntity, object, lockMode);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Object merge(Object object) throws HibernateException {
        try {
            return this.getDelegatedSession().merge(object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Object merge(String entityName, Object object) throws HibernateException {
        try {
            return this.getDelegatedSession().merge(entityName, object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void persist(Object object) throws HibernateException {
        try {
            this.getDelegatedSession().persist(object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void persist(String entityName, Object object) throws HibernateException {
        try {
            this.getDelegatedSession().persist(entityName, object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void reconnect() throws HibernateException {
        try {
            this.getDelegatedSession().reconnect();
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void reconnect(Connection conn) throws HibernateException {
        try {
            this.getDelegatedSession().reconnect(conn);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void refresh(Object object) throws HibernateException {
        try {
            this.getDelegatedSession().refresh(object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void refresh(Object object, LockMode lockMode) throws HibernateException {
        try {
            this.getDelegatedSession().refresh(object, lockMode);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException {
        try {
            this.getDelegatedSession().replicate(object, replicationMode);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void replicate(String entityName, Object object, ReplicationMode replicationMode) throws HibernateException {
        try {
            this.getDelegatedSession().replicate(entityName, object, replicationMode);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Serializable save(Object object) throws HibernateException {
        try {
            return this.getDelegatedSession().save(object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void save(Object object, Serializable id) throws HibernateException {
        try {
            this.getDelegatedSession().save(object, id);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public Serializable save(String entityName, Object object) throws HibernateException {
        try {
            return this.getDelegatedSession().save(entityName, object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void save(String entityName, Object object, Serializable id) throws HibernateException {
        try {
            this.getDelegatedSession().save(entityName, object, id);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void saveOrUpdate(Object object) throws HibernateException {
        try {
            this.getDelegatedSession().saveOrUpdate(object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void saveOrUpdate(String entityName, Object object) throws HibernateException {
        try {
            this.getDelegatedSession().saveOrUpdate(entityName, object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void setCacheMode(CacheMode cacheMode) {
        try {
            this.getDelegatedSession().setCacheMode(cacheMode);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void setFlushMode(FlushMode value) {
        try {
            this.getDelegatedSession().setFlushMode(value);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void update(Object object) throws HibernateException {
        try {
            this.getDelegatedSession().update(object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void update(Object object, Serializable id) throws HibernateException {
        try {
            this.getDelegatedSession().update(object, id);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void update(String entityName, Object object) throws HibernateException {
        try {
            this.getDelegatedSession().update(entityName, object);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

    public void update(String entityName, Object object, Serializable id) throws HibernateException {
        try {
            this.getDelegatedSession().update(entityName, object, id);
        } catch (HibernateException ex) {
            this.invalidateDelegatedSession();
            throw this.handleException(ex);
        }
    }

}
