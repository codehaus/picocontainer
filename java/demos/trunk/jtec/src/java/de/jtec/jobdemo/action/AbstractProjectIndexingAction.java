/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import de.jtec.jobdemo.search.DocumentFactory;
import de.jtec.jobdemo.search.DocumentIndexer;
import de.jtec.jobdemo.ProjectManager;
import de.jtec.jobdemo.beans.Project;

/**
 * index specified project.
 *
 * @author    kostik
 * @created   December 1, 2004
 * @version   $Revision$
 */
public abstract class AbstractProjectIndexingAction extends BaseIndexAction {

    private Integer _projectId;

    private ProjectManager _projectManager;


    /**
     * Constructor for the IndexProjectAction object
     *
     * @param factory  Description of Parameter
     * @param indexer  Description of Parameter
     * @param manager  Description of Parameter
     */
    public AbstractProjectIndexingAction(DocumentFactory factory, DocumentIndexer indexer, ProjectManager manager) {
        super(factory, indexer);
        setProjectManager(manager);
    }


    /**
     * Gets the ProjectManager attribute of the IndexProjectAction object
     *
     * @return   The ProjectManager value
     */
    public ProjectManager getProjectManager() {
        return _projectManager;
    }


    /**
     * Gets the ProjectId attribute of the IndexProjectAction object
     *
     * @return   The ProjectId value
     */
    public Integer getProjectId() {
        return _projectId;
    }


    /**
     * Sets the ProjectManager attribute of the IndexProjectAction object
     *
     * @param projectManager  The new ProjectManager value
     */
    public void setProjectManager(ProjectManager projectManager) {
        _projectManager = projectManager;
    }


    /**
     * Sets the ProjectId attribute of the IndexProjectAction object
     *
     * @param projectId  The new ProjectId value
     */
    public void setProjectId(Integer projectId) {
        _projectId = projectId;
    }


    /**
     * Gets the Project attribute of the IndexProjectAction object
     *
     * @return               The Project value
     * @exception Exception  Description of Exception
     */
    Project getProject() throws Exception {
        return getProjectManager().getProject(getProjectId());
    }

}
