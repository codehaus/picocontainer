/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import webwork.action.CommandDriven;
import de.jtec.jobdemo.ProfileManager;

/**
 * action to perform manipulation of profiles
 *
 * @author           kostik
 * @created          November 24, 2004
 * @version          $Revision$
 * @webwork.action   name="createProfile" success="indexProfile.action"
 *      error="/failure.vm" input="/profiles/create.vm"
 */
public class CreateProfileAction extends BaseProfileAction implements CommandDriven {

    /**
     * Constructor for the ProfileAction object
     *
     * @param manager  Description of Parameter
     */
    public CreateProfileAction(ProfileManager manager) {
        super(manager);
    }


    /**
     * create new profile instance
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {

        try {
            getProfileManager().createProfile(getProfile());
            setProfileId(getProfile().getId());
            return SUCCESS;
        } catch (Exception ex) {
            addErrorMessage(ex.toString());
            return ERROR;
        }
    }

}
