/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.hibernate;

import de.jtec.jobdemo.beans.Project;
import de.jtec.jobdemo.ProjectManager;
import de.jtec.jobdemo.StructureException;
import java.io.Serializable;
import java.util.Collection;
import net.sf.hibernate.Session;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;

/**
 * hibernate project manager.
 *
 * @author    $author$
 * @created   November 20, 2004
 * @version   $Revision$
 */
public class HibernateProjectManager extends AbstractBaseDao implements ProjectManager {
    /**
     * create project manager from hibernate session
     *
     * @param session  hibernate session
     */
    public HibernateProjectManager(Session session) {
        super(session);
    }


    /**
     * DOCUMENT ME!
     *
     * @param id                   DOCUMENT ME!
     * @return                     DOCUMENT ME!
     * @throws StructureException  DOCUMENT ME!
     */
    public Project getProject(Serializable id) throws StructureException {
        return (Project) getEntity(id, Project.class);
    }


    /**
     * provide all proejcts sorted by name
     *
     * @return                     DOCUMENT ME!
     * @throws StructureException  DOCUMENT ME!
     */
    public Collection getProjects() throws StructureException {
        try {
            Query query = getNamedQuery("allByName");
            return query.list();
        } catch (HibernateException he) {
            throw new StructureException(he);
        }
    }


    /**
     * DOCUMENT METHOD
     *
     * @param id                      Description of Parameter
     * @return                        Description of the Returned Value
     * @exception StructureException  Description of Exception
     */
    public Project loadProject(String id) throws StructureException {
        return getProject(Integer.decode(id));
    }


    /**
     * DOCUMENT ME!
     *
     * @param project              DOCUMENT ME!
     * @return                     DOCUMENT ME!
     * @throws StructureException  DOCUMENT ME!
     * @throws SecurityException   DOCUMENT ME!
     */
    public Project createProject(Project project) throws StructureException, SecurityException {
        createEntity(project);
        return project;
    }


    /**
     * DOCUMENT ME!
     *
     * @param project              DOCUMENT ME!
     * @throws StructureException  DOCUMENT ME!
     * @throws SecurityException   DOCUMENT ME!
     */
    public void removeProject(Project project) throws StructureException, SecurityException {
        removeEntity(project);
    }


    /**
     * update existing project
     *
     * @param project              DOCUMENT ME!
     * @throws StructureException  DOCUMENT ME!
     * @throws SecurityException   DOCUMENT ME!
     */
    public void updateProject(Project project) throws StructureException, SecurityException {
        updateEntity(project);
    }
}
