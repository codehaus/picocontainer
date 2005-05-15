/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import webwork.action.CommandDriven;
import de.jtec.jobdemo.beans.Profile;
import de.jtec.jobdemo.ProfileManager;

/**
 * action to perform manipulation of profiles
 *
 * @author           kostik
 * @created          November 24, 2004
 * @version          $Revision$
 * @webwork.action   name="removeProfile" success="redirect.action?url=/profiles/index.vm"
 *      error="/failure.vm" input="/profiles/edit.vm"
 */
public class RemoveProfileAction extends BaseProfileAction implements CommandDriven {

    /**
     * Constructor for the ProfileAction object
     *
     * @param manager  Description of Parameter
     */
    public RemoveProfileAction(ProfileManager manager) {
        super(manager);
    }


    /**
     * remove profile out of existence
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            getProfileManager().removeProfile(getProfileManager().getProfile(getProfileId()));
            return SUCCESS;
        } catch (Exception ex) {
            addErrorMessage(ex.toString());
            return ERROR;
        }
    }
}
