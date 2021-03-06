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
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.picocontainer.PicoInitializationException;

/**
 * Delegates everything to session factory obtained from confiuration. this class is necessary
 * because component adapters are really ugly when it comes to scripting.
 * 
 * @version $Id: SessionFactoryDelegator.java 2158 2005-07-08 02:13:36Z juze $
 */
public class SessionFactoryDelegator implements SessionFactory {

    private SessionFactory sessionFactory;

    public SessionFactoryDelegator(Configuration configuration) {
        try {
            this.sessionFactory = configuration.buildSessionFactory();
        } catch (HibernateException ex) {
            throw new PicoInitializationException(ex);
        }
    }

    public SessionFactory getDelegatedSessionFactory() {
        return this.sessionFactory;
    }

    public void close() throws HibernateException {
        this.getDelegatedSessionFactory().close();
    }

    public void evict(Class persistentClass) throws HibernateException {
        this.getDelegatedSessionFactory().evict(persistentClass);
    }

    public void evict(Class persistentClass, Serializable id) throws HibernateException {
        this.getDelegatedSessionFactory().evict(persistentClass, id);
    }

    public void evictCollection(String roleName) throws HibernateException {
        this.getDelegatedSessionFactory().evictCollection(roleName);
    }

    public void evictCollection(String roleName, Serializable id) throws HibernateException {
        this.getDelegatedSessionFactory().evictCollection(roleName, id);
    }

    public void evictEntity(String entityName) throws HibernateException {
        this.getDelegatedSessionFactory().evictEntity(entityName);
    }

    public void evictEntity(String entityName, Serializable id) throws HibernateException {
        this.getDelegatedSessionFactory().evictEntity(entityName, id);
    }

    public void evictQueries() throws HibernateException {
        this.getDelegatedSessionFactory().evictQueries();
    }

    public void evictQueries(String cacheRegion) throws HibernateException {
        this.getDelegatedSessionFactory().evictQueries(cacheRegion);
    }

    public Map getAllClassMetadata() throws HibernateException {
        return this.getDelegatedSessionFactory().getAllClassMetadata();
    }

    public Map getAllCollectionMetadata() throws HibernateException {
        return this.getDelegatedSessionFactory().getAllCollectionMetadata();
    }

    public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
        return this.getDelegatedSessionFactory().getClassMetadata(persistentClass);
    }

    public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
        return this.getDelegatedSessionFactory().getClassMetadata(entityName);
    }

    public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
        return this.getDelegatedSessionFactory().getCollectionMetadata(roleName);
    }

	public Session getCurrentSession() throws HibernateException {
		return this.getDelegatedSessionFactory().getCurrentSession();
	}

    public Reference getReference() throws NamingException {
        return this.getDelegatedSessionFactory().getReference();
    }

    public Statistics getStatistics() {
        return this.getDelegatedSessionFactory().getStatistics();
    }

	public boolean isClosed() {
		return this.getDelegatedSessionFactory().isClosed();
	}

    public Session openSession() throws HibernateException {
        return this.getDelegatedSessionFactory().openSession();
    }

    public Session openSession(Connection connection) {
        return this.getDelegatedSessionFactory().openSession(connection);
    }

    public Session openSession(Connection connection, Interceptor interceptor) {
        return this.getDelegatedSessionFactory().openSession(connection, interceptor);
    }

    public Session openSession(Interceptor interceptor) throws HibernateException {
        return this.getDelegatedSessionFactory().openSession(interceptor);
    }

    /**
     * {@inheritDoc}
     * @return a set of defined filter names.
     * @see org.hibernate.SessionFactory#getDefinedFilterNames()
     */
    public Set getDefinedFilterNames() {
        return this.getDelegatedSessionFactory().getDefinedFilterNames();
    }

    /**
     * {@inheritDoc}
     * @param filterName
     * @return FilterDefinition
     * @throws HibernateException
     * @see org.hibernate.SessionFactory#getFilterDefinition(java.lang.String)
     */
    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        return this.getDelegatedSessionFactory().getFilterDefinition(filterName);
    }

    /**
     * {@inheritDoc}
     * @return StatelessSession
     * @see org.hibernate.SessionFactory#openStatelessSession()
     */
    public StatelessSession openStatelessSession() {
        return this.getDelegatedSessionFactory().openStatelessSession();
    }

    /**
     * {@inheritDoc}
     * @param connection
     * @return StatelessSession
     * @see org.hibernate.SessionFactory#openStatelessSession(java.sql.Connection)
     */
    public StatelessSession openStatelessSession(Connection connection) {
        return this.getDelegatedSessionFactory().openStatelessSession(connection);
    }

}
