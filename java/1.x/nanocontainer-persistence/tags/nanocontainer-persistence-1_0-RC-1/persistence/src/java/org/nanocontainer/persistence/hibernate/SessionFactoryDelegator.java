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
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.Reference;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.picocontainer.PicoInitializationException;

/**
 * Delegates everything to session factory obtained from confiuration. this class is necessary
 * because component adapters are really ugly when it comes to scripting.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
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

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public void close() throws HibernateException {
        this.getSessionFactory().close();
    }

    public void evict(Class persistentClass) throws HibernateException {
        this.getSessionFactory().evict(persistentClass);
    }

    public void evict(Class persistentClass, Serializable id) throws HibernateException {
        this.getSessionFactory().evict(persistentClass, id);
    }

    public void evictCollection(String roleName) throws HibernateException {
        this.getSessionFactory().evictCollection(roleName);
    }

    public void evictCollection(String roleName, Serializable id) throws HibernateException {
        this.getSessionFactory().evictCollection(roleName, id);
    }

    public void evictEntity(String entityName) throws HibernateException {
        this.getSessionFactory().evictEntity(entityName);
    }

    public void evictEntity(String entityName, Serializable id) throws HibernateException {
        this.getSessionFactory().evictEntity(entityName, id);
    }

    public void evictQueries() throws HibernateException {
        this.getSessionFactory().evictQueries();
    }

    public void evictQueries(String cacheRegion) throws HibernateException {
        this.getSessionFactory().evictQueries(cacheRegion);
    }

    public Map getAllClassMetadata() throws HibernateException {
        return this.getSessionFactory().getAllClassMetadata();
    }

    public Map getAllCollectionMetadata() throws HibernateException {
        return this.getSessionFactory().getAllCollectionMetadata();
    }

    public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
        return this.getSessionFactory().getClassMetadata(persistentClass);
    }

    public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
        return this.getSessionFactory().getClassMetadata(entityName);
    }

    public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
        return this.getSessionFactory().getCollectionMetadata(roleName);
    }

    public Reference getReference() throws NamingException {
        return this.getSessionFactory().getReference();
    }

    public Statistics getStatistics() {
        return this.getSessionFactory().getStatistics();
    }

    public Session openSession() throws HibernateException {
        return this.getSessionFactory().openSession();
    }

    public Session openSession(Connection connection) {
        return this.getSessionFactory().openSession(connection);
    }

    public Session openSession(Connection connection, Interceptor interceptor) {
        return this.getSessionFactory().openSession(connection, interceptor);
    }

    public Session openSession(Interceptor interceptor) throws HibernateException {
        return this.getSessionFactory().openSession(interceptor);
    }

}
