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
 * @webwork.action   name="removeProject" success="redirect.action?url=/projects/index.vm"
 *      error="/failure.vm" input="/projects/edit.vm"
 *      forwards="show=/projects/show.vm"
 */
public class RemoveProjectAction extends BaseProjectAction implements CommandDriven {

    /**
     * Constructor for the BaseProjectAction object
     *
     * @param manager  Description of Parameter
     */
    public RemoveProjectAction(ProjectManager manager) {
        super(manager);
    }



    /**
     * remove project out of existence
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            getProjectManager().removeProject(getProjectManager().getProject(getProjectId()));
            return SUCCESS;
        } catch (Exception ex) {
            addErrorMessage(ex.toString());
            return ERROR;
        }
    }
}
