/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo;

import de.jtec.jobdemo.beans.Project;
import de.jtec.jobdemo.beans.Profile;
import de.jtec.jobdemo.beans.Request;
import java.io.Serializable;

/**
 * managers project requests
 *
 * @author    kostik
 * @created   November 23, 2004
 * @version   $Revision$
 */
public interface RequestManager {

    /**
     * request project for given profile
     *
     * @param project                 project to be requested
     * @param profile                 requesting profile
     * @return                        project request
     * @exception StructureException  thrown on internal exception 
     */
    Request requestProject(Project project, Profile profile) throws StructureException;


    /**
     * cancel project request 
     *
     * @param request                 project request to be canceled
     * @exception StructureException  thrown on internal exceptions
     */
    void cancelRequest(Request request) throws StructureException;


    /**
     * retrieve request by id
     *
     * @param requestId               id of rewuest to be retrieved
     * @return                        project request
     * @exception StructureException  thrown if something goes wrong internally
     */
    Request getRequest(Serializable requestId) throws StructureException;
}

