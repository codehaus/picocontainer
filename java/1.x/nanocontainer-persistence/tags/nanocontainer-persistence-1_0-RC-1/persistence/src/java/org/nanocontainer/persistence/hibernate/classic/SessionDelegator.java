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

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.sf.hibernate.Criteria;
import net.sf.hibernate.FlushMode;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.Query;
import net.sf.hibernate.ReplicationMode;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.type.Type;

/** 
 * abstract base class for session delegators. delegates all calls to session obtained by 
 * implementing class. error handling is also there. All methods are just delegations to
 * hibernate session. 
 * 
 * 
 * @author Konstantin Pribluda
 * @version $Revision$ 
 */
public abstract class SessionDelegator implements Session {
    
    /**
     * obtain hibernate session.
     */
    public abstract Session getSession();
    /**
     * perform actions to dispose "burned" session properly
     */
    public abstract void invalidateSession() throws HibernateException;
    
    public void flush() throws HibernateException {
        try {
            getSession().flush();
        } catch(HibernateException ex)  {
            invalidateSession();
            throw ex;
        }
    }
    
    public void setFlushMode(FlushMode flushMode) {
        getSession().setFlushMode(flushMode);
    }
    
    public FlushMode getFlushMode() {
        return getSession().getFlushMode();
    }
    
    public SessionFactory getSessionFactory() {
        return getSession().getSessionFactory();
    }
    
    public Connection connection() throws HibernateException {
        try {
            return getSession().connection();
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }
    }
    
    public Connection disconnect() throws HibernateException {
       try {
            return getSession().disconnect();
       } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
       }
    }
    
    public void reconnect() throws HibernateException {
       try {
            getSession().reconnect();
       } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
       }   
    }
    
    public void reconnect(Connection connection) throws HibernateException {
       try {
            getSession().reconnect(connection);
       } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
       }   
    }
    
    public Connection close() throws HibernateException {
       try {
            return getSession().close();
       } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
       }   
    }
    
    public void cancelQuery() throws HibernateException {
       try {
            getSession().cancelQuery();
       } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
       }   
    }
    
    public boolean isOpen() {
        return getSession().isOpen();
    }
    
    public boolean isConnected() {
        return getSession().isConnected();
    }
    
    public boolean isDirty() throws HibernateException {
       try {
            return getSession().isDirty();
       } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
       }   
    }
    
    
    public Serializable getIdentifier(Object object) throws HibernateException {
        try {
            return getSession().getIdentifier(object);
       } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
       }          
    }
    
    public boolean contains(Object object) {
        return getSession().contains(object);
    }
    
    public void evict(Object object) throws HibernateException {
       try {
             getSession().evict(object);
       } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
       }          
    }
    
    public Object load(Class theClass, Serializable id, LockMode lockMode) throws HibernateException {
        try {
            return getSession().load(theClass,id,lockMode);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    public Object load(Class theClass, Serializable id) throws HibernateException {
        try {
            return getSession().load(theClass,id);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public void load(Object object, Serializable id) throws HibernateException {
        try {
            getSession().load(object,id);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException {
        try {
            getSession().replicate(object,replicationMode);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public Serializable save(Object object) throws HibernateException {
        try {
            return getSession().save(object);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public void save(Object object, Serializable id) throws HibernateException {
        try {
             getSession().save(object,id);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public void saveOrUpdate(Object object) throws HibernateException {
        try {
             getSession().saveOrUpdate(object);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public void update(Object object) throws HibernateException {
        try {
             getSession().update(object);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public void update(Object object, Serializable id) throws HibernateException {
        try {
             getSession().update(object,id);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public Object saveOrUpdateCopy(Object object) throws HibernateException  {
        try {
             return getSession().saveOrUpdateCopy(object);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public Object saveOrUpdateCopy(Object object, Serializable id) throws HibernateException {
        try {
             return getSession().saveOrUpdateCopy(object,id);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public void delete(Object object) throws HibernateException {
        try {
              getSession().delete(object);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public List find(String query) throws HibernateException {
        try {
             return getSession().find(query);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public List find(String query, Object value, Type type) throws HibernateException {
        try {
             return getSession().find(query,value,type);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public List find(String query, Object[] values, Type[] types) throws HibernateException {
        try {
             return getSession().find(query,values,types);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public Iterator iterate(String query) throws HibernateException {
        try {
             return getSession().iterate(query);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }          
    }
    
    public Iterator iterate(String query, Object value, Type type) throws HibernateException {
        try {
             return getSession().iterate(query,value,type);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public Iterator iterate(String query, Object[] values, Type[] types) throws HibernateException {
        try {
             return getSession().iterate(query,values,types);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public Collection filter(Object collection, String filter) throws HibernateException {
        try {
             return getSession().filter(collection,filter);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public Collection filter(Object collection, String filter, Object value, Type type) throws HibernateException {
        try {
             return getSession().filter(collection,filter,value,type);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    
    public Collection filter(Object collection, String filter, Object[] values, Type[] types) throws HibernateException {
        try {
             return getSession().filter(collection,filter,values,types);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public int delete(String query) throws HibernateException {
        try {
             return getSession().delete(query);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public int delete(String query, Object value, Type type) throws HibernateException {
        try {
             return getSession().delete(query,value,type);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
        
    }
    
    public int delete(String query, Object[] values, Type[] types) throws HibernateException {
        try {
             return getSession().delete(query,values,types);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public void lock(Object object, LockMode lockMode) throws HibernateException {
        try {
              getSession().lock(object,lockMode);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public void refresh(Object object) throws HibernateException {
        try {
              getSession().refresh(object);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public void refresh(Object object, LockMode lockMode) throws HibernateException {
        try {
              getSession().refresh(object,lockMode);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }

    public LockMode getCurrentLockMode(Object object) throws HibernateException {
        try {
              return getSession().getCurrentLockMode(object);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public Transaction beginTransaction() throws HibernateException {
        try {
              return getSession().beginTransaction();
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public Criteria createCriteria(Class persistentClass) {
        return getSession().createCriteria(persistentClass);
    }
    
    public Query createQuery(String queryString) throws HibernateException {
        try {
              return getSession().createQuery(queryString);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
        
    
    public Query createFilter(Object collection, String queryString) throws HibernateException {
        try {
              return getSession().createFilter(collection,queryString);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public Query getNamedQuery(String queryName) throws HibernateException {
        try {
              return getSession().getNamedQuery(queryName);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public Query createSQLQuery(String sql, String returnAlias, Class returnClass) {
        return getSession().createSQLQuery(sql,returnAlias,returnClass);        
    }
    
    public Query createSQLQuery(String sql, String[] returnAliases, Class[] returnClasses) {
        return getSession().createSQLQuery(sql,returnAliases,returnClasses);
    }
    
    public void clear() {
        getSession().clear();
    }
    
    public Object get(Class clazz, Serializable id) throws HibernateException {
        try {
            return getSession().get(clazz,id);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
    }
    
    public Object get(Class clazz, Serializable id, LockMode lockMode) throws HibernateException {
      try {
            return getSession().get(clazz,id,lockMode);
        } catch(HibernateException ex) {
            invalidateSession();
            throw ex;
        }                  
     }
}
