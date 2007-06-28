/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import webwork.action.ActionSupport;
import de.jtec.jobdemo.beans.Profile;
import de.jtec.jobdemo.ProfileManager;

/**
 * base action to perform manipulation of profiles
 *
 * @author    kostik
 * @created   November 25, 2004
 * @version   $Revision$
 */
public abstract class BaseProfileAction extends ActionSupport {

    private Profile _profile = new Profile();

    private ProfileManager _profileManager;

    private Integer _profileId;


    /**
     * Constructor for the ProfileAction object
     *
     * @param manager  Description of Parameter
     */
    public BaseProfileAction(ProfileManager manager) {
        setProfileManager(manager);
    }


    /**
     * Gets the Profile attribute of the ProfileAction object
     *
     * @return   The Profile value
     */
    public Profile getProfile() {
        return _profile;
    }


    /**
     * Gets the ProfileManager attribute of the ProfileAction object
     *
     * @return   The ProfileManager value
     */
    public ProfileManager getProfileManager() {
        return _profileManager;
    }


    /**
     * Gets the ProfileId attribute of the ProfileAction object
     *
     * @return   The ProfileId value
     */
    public Integer getProfileId() {
        return _profileId;
    }


    /**
     * Sets the Profile attribute of the ProfileAction object
     *
     * @param profile  The new Profile value
     */
    public void setProfile(Profile profile) {
        _profile = profile;
    }


    /**
     * Sets the ProfileManager attribute of the ProfileAction object
     *
     * @param profileManager  The new ProfileManager value
     */
    public void setProfileManager(ProfileManager profileManager) {
        _profileManager = profileManager;
    }


    /**
     * Sets the ProfileId attribute of the ProfileAction object
     *
     * @param profileId  The new ProfileId value
     */
    public void setProfileId(Integer profileId) {
        _profileId = profileId;
    }

}
