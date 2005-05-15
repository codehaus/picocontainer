/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.hibernate;

import de.jtec.jobdemo.beans.BaseEntity;
import de.jtec.jobdemo.StructureException;
import java.io.Serializable;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Query;
/**
 * base hibernate fuctionality
 *
 * @author    kostik
 * @created   November 20, 2004
 * @version   $Revision$
 */
abstract class AbstractBaseDao {
    private Session _session;


    /**
     * Creates a new AbstractBaseDao object.
     *
     * @param session  hibernate session to work with
     */
    public AbstractBaseDao(Session session) {
        setSession(session);
    }


    /**
     * provide hiernate session
     *
     * @return   DOCUMENT ME!
     */
    Session getSession() {
        return _session;
    }


    /**
     * Gets the Entity attribute of the AbstractBaseDao object
     *
     * @param id                      Description of Parameter
     * @param clazz                   Description of Parameter
     * @return                        The Entity value
     * @exception StructureException  Description of Exception
     */
    Object getEntity(Serializable id, Class clazz) throws StructureException {
        try {
            return getSession().get(clazz, id);
        } catch (HibernateException ex) {
            throw new StructureException(ex);
        }
    }


    /**
     * retrieve named hibernate query
     *
     * @param queryName               Description of Parameter
     * @return                        The NamedQuery value
     * @exception HibernateException  Description of Exception
     */
    Query getNamedQuery(String queryName) throws HibernateException {
        return getSession().getNamedQuery(queryName);
    }


    /**
     * DOCUMENT ME!
     *
     * @param session  DOCUMENT ME!
     */
    void setSession(Session session) {
        _session = session;
    }


    /**
     * create new persistent entity
     *
     * @param entity                  Description of Parameter
     * @exception StructureException  Description of Exception
     */
    void createEntity(BaseEntity entity) throws StructureException {
        try {
            getSession().save(entity);
        } catch (HibernateException he) {
            throw new StructureException(he);
        }
    }


    /**
     * DOCUMENT METHOD
     *
     * @param entity                  Description of Parameter
     * @exception StructureException  Description of Exception
     */
    void updateEntity(BaseEntity entity) throws StructureException {
        try {
            getSession().update(entity);
        } catch (HibernateException he) {
            throw new StructureException(he);
        }
    }


    /**
     * DOCUMENT METHOD
     *
     * @param entity                  Description of Parameter
     * @exception StructureException  Description of Exception
     */
    void removeEntity(BaseEntity entity) throws StructureException {
        try {
            getSession().delete(entity);
        } catch (HibernateException he) {
            throw new StructureException(he);
        }
    }
}
