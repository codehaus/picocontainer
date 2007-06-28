/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import webwork.action.ActionSupport;
import webwork.action.CommandDriven;
import de.jtec.jobdemo.ProjectManager;
import de.jtec.jobdemo.beans.Project;
/**
 * action for project manipulation
 *
 * @author           kostik
 * @created          November 23, 2004
 * @version          $Revision$
 * @webwork.action   name="updateProject" success="indexProject.action"
 *      error="/failure.vm" input="/projects/edit.vm"
 *      forwards="show=/projects/show.vm"
 */
public class UpdateProjectAction extends BaseProjectAction implements CommandDriven {

    /**
     * Constructor for the BaseProjectAction object
     *
     * @param manager  Description of Parameter
     */
    public UpdateProjectAction(ProjectManager manager) {
        super(manager);
    }



    /**
     * update existing project
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            getProject().setId(getProjectId());
            getProjectManager().updateProject(getProject());
            return SUCCESS;
        } catch (Exception ex) {
            addErrorMessage(ex.toString());
            return ERROR;
        }
    }

}
