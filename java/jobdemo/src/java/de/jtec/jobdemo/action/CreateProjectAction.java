/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import webwork.action.CommandDriven;
import de.jtec.jobdemo.ProjectManager;
/**
 * action for project manipulation
 *
 * @author           kostik
 * @created          November 23, 2004
 * @version          $Revision$
 * @webwork.action   name="createProject" success="indexProject.action"
 *      error="/failure.vm" input="/projects/edit.vm"
 */
public class CreateProjectAction extends BaseProjectAction implements CommandDriven {

    /**
     * Constructor for the BaseProjectAction object
     *
     * @param manager  Description of Parameter
     */
    public CreateProjectAction(ProjectManager manager) {
        super(manager);
    }



    /**
     * create new project instance
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            getProjectManager().createProject(getProject());
            setProjectId(getProject().getId());
            return SUCCESS;
        } catch (Exception ex) {
            addErrorMessage(ex.toString());
            return ERROR;
        }
    }
}
