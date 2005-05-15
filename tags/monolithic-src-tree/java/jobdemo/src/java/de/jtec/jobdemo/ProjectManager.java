/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo;

import de.jtec.jobdemo.beans.Project;

import java.io.Serializable;

import java.util.Collection;

/**
 * interface for project manager - manage project descriptions
 *
 * @author    Konstantin Pribluda
 * @created   November 20, 2004
 * @version   $Revision$
 */
public interface ProjectManager {
    /**
     * retrieve project by ID
     *
     * @param id                      id to be retrieved
     * @return                        project bean instance or null if not found
     * @exception StructureException  thrown if something goes wrong internally
     */
    Project getProject(Serializable id) throws StructureException;


    /**
     * load project by id as string ( usefull for templates and actions)
     *
     * @param id                      string form of id
     * @return                        project bean or null if not found
     * @exception StructureException  thrown if something goes wrong internally
     */
    Project loadProject(String id) throws StructureException;


    /**
     * retrieve list of projects
     *
     * @return                        collection of known projects
     * @exception StructureException  thrown if something goes wrong internally
     */
    Collection getProjects() throws StructureException;


    /**
     * create new project.
     *
     * @param project                 project bean to be persisted
     * @return                        persisted project bean
     * @exception StructureException  thrown if something goes wrong
     * @exception SecurityException   thrown if caller not allowed to do this
     */
    Project createProject(Project project) throws StructureException, SecurityException;


    /**
     * remove project out of existence
     *
     * @param project                 project to be removed
     * @exception StructureException  thrown if something goes wrong internally
     * @exception SecurityException   if caller is not allowed to do this
     */
    void removeProject(Project project) throws StructureException, SecurityException;


    /**
     * update persistent project
     *
     * @param project                 bean to be updated
     * @exception StructureException  thrown if something goes wrong internally
     * @exception SecurityException   thrown if user is not allowed to do this
     */
    void updateProject(Project project) throws StructureException, SecurityException;
}
