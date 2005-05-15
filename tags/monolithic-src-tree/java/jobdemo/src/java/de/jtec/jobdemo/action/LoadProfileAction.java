/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import de.jtec.jobdemo.ProfileManager;

/**
 * action to perform manipulation of profiles
 *
 * @author           kostik
 * @created          November 24, 2004
 * @version          $Revision$
 * @webwork.action   name="showProfileProjects" success="/projects/profile.vm"
 *      error="/failure.vm"
 * @webwork.action   name="previewProfileRemove"
 *      success="/profiles/previewRemove.vm" error="/failure.vm"
 * @webwork.action   name="loadProfileEdit" success="/profiles/edit.vm"
 *      error="/failure.vm"
 * @webwork.action   name="showProfileProfiles" success="/profiles/profile.vm"
 *      error="/failure.vm"
 */
public class LoadProfileAction extends BaseProfileAction {

    /**
     * Constructor for the ProfileAction object
     *
     * @param manager  Description of Parameter
     */
    public LoadProfileAction(ProfileManager manager) {
        super(manager);
    }


    /**
     * load existing profile for further editing
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            setProfile(getProfileManager().getProfile(getProfileId()));
            return SUCCESS;
        } catch (Exception ex) {
            addErrorMessage(ex.toString());
            return ERROR;
        }
    }
}
