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
 * @webwork.action   name="updateProfile" success="indexProfile.action"
 *      error="/failure.vm" input="/profiles/edit.vm"
 */
public class UpdateProfileAction extends BaseProfileAction implements CommandDriven {

    /**
     * Constructor for the ProfileAction object
     *
     * @param manager  Description of Parameter
     */
    public UpdateProfileAction(ProfileManager manager) {
        super(manager);
    }


    /**
     * update existing profile
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            getProfile().setId(getProfileId());
            getProfileManager().updateProfile(getProfile());
            return SUCCESS;
        } catch (Exception ex) {
            addErrorMessage(ex.toString());
            return ERROR;
        }
    }

}
