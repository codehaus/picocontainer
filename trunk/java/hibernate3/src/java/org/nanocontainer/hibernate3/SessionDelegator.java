/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.hibernate3;

import java.io.Serializable;
import java.sql.Connection;

import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Abstract base class for session delegators. delegates all calls to session
 * obtained by implementing class. error handling is also there. All methods are
 * just delegations to hibernate session.
 * 
 * @author Konstantin Pribluda
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
 */
public abstract class SessionDelegator implements Session {

    /**
     * Obtain hibernate session.
     */
    public abstract Session getSession();

    /**
     * Perform actions to dispose "burned" session properly.
     */
    public abstract void invalidateSession() throws HibernateException;

    /**
     * Handle an HibernateException. The default behavior throw cause again.
     */
    public RuntimeException handleException(HibernateException cause) throws HibernateException {
        throw cause;
    }

    public Transaction beginTransaction() throws HibernateException {
        try {
            return this.getSession().beginTransaction();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void cancelQuery() throws HibernateException {
        try {
            this.getSession().cancelQuery();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void clear() {
        try {
            this.getSession().clear();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Connection close() throws HibernateException {
        try {
            return this.getSession().close();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Connection connection() throws HibernateException {
        try {
            return this.getSession().connection();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public boolean contains(Object object) {
        try {
            return this.getSession().contains(object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void create(Object object) throws HibernateException {
        try {
            this.getSession().create(object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void create(String entityName, Object object) throws HibernateException {
        try {
            this.getSession().create(entityName, object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Criteria createCriteria(Class persistentClass) {
        try {
            return this.getSession().createCriteria(persistentClass);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Criteria createCriteria(String entityName) {
        try {
            return this.getSession().createCriteria(entityName);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Query createFilter(Object collection, String queryString) throws HibernateException {
        try {
            return this.getSession().createFilter(collection, queryString);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Query createQuery(String queryString) throws HibernateException {
        try {
            return this.getSession().createQuery(queryString);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Query createSQLQuery(String sql, String returnAlias, Class returnClass) {
        try {
            return this.getSession().createSQLQuery(sql, returnAlias, returnClass);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Query createSQLQuery(String sql, String[] returnAliases, Class[] returnClasses) {
        try {
            return this.getSession().createSQLQuery(sql, returnAliases, returnClasses);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void delete(Object object) throws HibernateException {
        try {
            this.getSession().delete(object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void disableFilter(String filterName) {
        try {
            this.getSession().disableFilter(filterName);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Connection disconnect() throws HibernateException {
        try {
            return this.getSession().disconnect();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Filter enableFilter(String filterName) {
        try {
            return this.getSession().enableFilter(filterName);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void evict(Object object) throws HibernateException {
        try {
            this.getSession().evict(object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void flush() throws HibernateException {
        try {
            this.getSession().flush();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Object get(Class clazz, Serializable id) throws HibernateException {
        try {
            return this.getSession().get(clazz, id);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Object get(Class clazz, Serializable id, LockMode lockMode) throws HibernateException {
        try {
            return this.getSession().get(clazz, id, lockMode);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Object get(String entityName, Serializable id) throws HibernateException {
        try {
            return this.getSession().get(entityName, id);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
        try {
            return this.getSession().get(entityName, id, lockMode);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public LockMode getCurrentLockMode(Object object) throws HibernateException {
        try {
            return this.getSession().getCurrentLockMode(object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Filter getEnabledFilter(String filterName) {
        try {
            return this.getSession().getEnabledFilter(filterName);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public String getEntityName(Object object) throws HibernateException {
        try {
            return this.getSession().getEntityName(object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public FlushMode getFlushMode() {
        try {
            return this.getSession().getFlushMode();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Serializable getIdentifier(Object object) throws HibernateException {
        try {
            return this.getSession().getIdentifier(object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Query getNamedQuery(String queryName) throws HibernateException {
        try {
            return this.getSession().getNamedQuery(queryName);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public SessionFactory getSessionFactory() {
        try {
            return this.getSession().getSessionFactory();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public boolean isConnected() {
        try {
            return this.getSession().isConnected();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public boolean isDirty() throws HibernateException {
        try {
            return this.getSession().isDirty();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public boolean isOpen() {
        try {
            return this.getSession().isOpen();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Object load(Class theClass, Serializable id) throws HibernateException {
        try {
            return this.getSession().load(theClass, id);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Object load(Class theClass, Serializable id, LockMode lockMode) throws HibernateException {
        try {
            return this.getSession().load(theClass, id, lockMode);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void load(Object object, Serializable id) throws HibernateException {
        try {
            this.getSession().load(object, id);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Object load(String entityName, Serializable id) throws HibernateException {
        try {
            return this.getSession().load(entityName, id);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
        try {
            return this.getSession().load(entityName, id, lockMode);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void lock(Object object, LockMode lockMode) throws HibernateException {
        try {
            this.getSession().lock(object, lockMode);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void lock(String entityEntity, Object object, LockMode lockMode) throws HibernateException {
        try {
            this.getSession().lock(entityEntity, object, lockMode);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Object merge(Object object) throws HibernateException {
        try {
            return this.getSession().merge(object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Object merge(String entityName, Object object) throws HibernateException {
        try {
            return this.getSession().merge(entityName, object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void reconnect() throws HibernateException {
        try {
            this.getSession().reconnect();
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void reconnect(Connection conn) throws HibernateException {
        try {
            this.getSession().reconnect(conn);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void refresh(Object object) throws HibernateException {
        try {
            this.getSession().refresh(object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void refresh(Object object, LockMode lockMode) throws HibernateException {
        try {
            this.getSession().refresh(object, lockMode);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException {
        try {
            this.getSession().replicate(object, replicationMode);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void replicate(String entityName, Object object, ReplicationMode replicationMode) throws HibernateException {
        try {
            this.getSession().replicate(entityName, object, replicationMode);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Serializable save(Object object) throws HibernateException {
        try {
            return this.getSession().save(object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void save(Object object, Serializable id) throws HibernateException {
        try {
            this.getSession().save(object, id);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public Serializable save(String entityName, Object object) throws HibernateException {
        try {
            return this.getSession().save(entityName, object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void save(String entityName, Object object, Serializable id) throws HibernateException {
        try {
            this.getSession().save(entityName, object, id);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void saveOrUpdate(Object object) throws HibernateException {
        try {
            this.getSession().saveOrUpdate(object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void saveOrUpdate(String entityName, Object object) throws HibernateException {
        try {
            this.getSession().saveOrUpdate(entityName, object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void setFlushMode(FlushMode value) {
        try {
            this.getSession().setFlushMode(value);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void update(Object object) throws HibernateException {
        try {
            this.getSession().update(object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void update(Object object, Serializable id) throws HibernateException {
        try {
            this.getSession().update(object, id);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void update(String entityName, Object object) throws HibernateException {
        try {
            this.getSession().update(entityName, object);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

    public void update(String entityName, Object object, Serializable id) throws HibernateException {
        try {
            this.getSession().update(entityName, object, id);
        } catch (HibernateException ex) {
            this.invalidateSession();
            throw this.handleException(ex);
        }
    }

}
