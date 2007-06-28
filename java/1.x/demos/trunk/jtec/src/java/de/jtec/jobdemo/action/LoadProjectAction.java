/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import de.jtec.jobdemo.ProjectManager;
/**
 * action for project manipulation
 *
 * @author           kostik
 * @created          November 23, 2004
 * @version          $Revision$
 * @webwork.action   name="showProjectProjects" success="/projects/project.vm"
 *      error="/failure.vm"
 * @webwork.action   name="previewProjectRemove"
 *      success="/projects/previewRemove.vm" error="/failure.vm"
 * @webwork.action   name="loadProjectEdit" success="/projects/edit.vm"
 *      error="/failure.vm"
 * @webwork.action   name="showProjectProfiles" success="/profiles/project.vm"
 *      error="/failure.vm"
 */
public class LoadProjectAction extends BaseProjectAction {

    /**
     * Constructor for the BaseProjectAction object
     *
     * @param manager  Description of Parameter
     */
    public LoadProjectAction(ProjectManager manager) {
        super(manager);
    }


    /**
     * load existing project for editing
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            setProject(getProjectManager().getProject(getProjectId()));
            return SUCCESS;
        } catch (Exception ex) {
            addErrorMessage(ex.toString());
            return ERROR;
        }
    }

}
