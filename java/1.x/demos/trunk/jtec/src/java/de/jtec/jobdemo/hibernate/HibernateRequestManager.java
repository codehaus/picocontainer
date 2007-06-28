/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.hibernate;

import de.jtec.jobdemo.beans.Project;
import de.jtec.jobdemo.beans.Profile;
import de.jtec.jobdemo.beans.Request;
import de.jtec.jobdemo.RequestManager;
import de.jtec.jobdemo.StructureException;
import java.io.Serializable;
import net.sf.hibernate.Session;

/**
 * hibernate project manager.
 *
 * @author    $author$
 * @created   November 20, 2004
 * @version   $Revision$
 */
public class HibernateRequestManager extends AbstractBaseDao implements RequestManager {
    /**
     * create project manager from hibernate session
     *
     * @param session  hibernate session
     */
    public HibernateRequestManager(Session session) {
        super(session);
    }


    /**
     * Gets the Request attribute of the HibernateRequestManager object
     *
     * @param id                      Description of Parameter
     * @return                        The Request value
     * @exception StructureException  Description of Exception
     */
    public Request getRequest(Serializable id) throws StructureException {
        return (Request) getEntity(id, Request.class);
    }


    /**
     * create new request and weawe it properly. requesting project second time
     * does not change anything, and original request is returned
     *
     * @param project                 Description of Parameter
     * @param profile                 Description of Parameter
     * @return                        Description of the Returned Value
     * @exception StructureException  Description of Exception
     */
    public Request requestProject(Project project, Profile profile) throws StructureException {
        if (project.getRequests().get(profile) != null) {
            return (Request) project.getRequests().get(profile);
        }
        Request request = new Request(profile, project);
        createEntity(request);
        profile.getRequests().put(project, request);
        project.getRequests().put(profile, request);
        return request;
    }


    /**
     * cabncel project request - remove entity and disconect relations
     *
     * @param request                 Description of Parameter
     * @exception StructureException  Description of Exception
     */
    public void cancelRequest(Request request) throws StructureException {
        request.getProject().getRequests().remove(request.getProfile());
        request.getProfile().getRequests().remove(request.getProject());
        removeEntity(request);
    }
}
