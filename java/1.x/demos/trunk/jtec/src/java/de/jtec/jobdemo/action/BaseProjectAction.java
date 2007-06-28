/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import webwork.action.ActionSupport;
import de.jtec.jobdemo.ProjectManager;
import de.jtec.jobdemo.beans.Project;
/**
 * action for project manipulation
 *
 * @author    kostik
 * @created   November 25, 2004
 * @version   $Revision$
 */
public abstract class BaseProjectAction extends ActionSupport {

    private ProjectManager _projectManager;

    private Project _project = new Project();

    private Integer _projectId;


    /**
     * Constructor for the BaseProjectAction object
     *
     * @param manager  Description of Parameter
     */
    public BaseProjectAction(ProjectManager manager) {
        setProjectManager(manager);
    }


    /**
     * Gets the ProjectManager attribute of the BaseProjectAction object
     *
     * @return   The ProjectManager value
     */
    public ProjectManager getProjectManager() {
        return _projectManager;
    }


    /**
     * Gets the Project attribute of the BaseProjectAction object
     *
     * @return   The Project value
     */
    public Project getProject() {
        return _project;
    }


    /**
     * Gets the ProjectId attribute of the BaseProjectAction object
     *
     * @return   The ProjectId value
     */
    public Integer getProjectId() {
        return _projectId;
    }


    /**
     * Sets the ProjectManager attribute of the BaseProjectAction object
     *
     * @param projectManager  The new ProjectManager value
     */
    public void setProjectManager(ProjectManager projectManager) {
        _projectManager = projectManager;
    }


    /**
     * id of project in question
     *
     * @param project  The new Project value
     */
    public void setProject(Project project) {
        _project = project;
    }


    /**
     * Sets the ProjectId attribute of the BaseProjectAction object
     *
     * @param projectId  The new ProjectId value
     */
    public void setProjectId(Integer projectId) {
        _projectId = projectId;
    }

}
