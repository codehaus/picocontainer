/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import de.jtec.jobdemo.search.DocumentFactory;
import de.jtec.jobdemo.search.DocumentIndexer;
import de.jtec.jobdemo.ProfileManager;
import de.jtec.jobdemo.beans.Profile;

/**
 * index specified profile.
 *
 * @author    kostik
 * @created   December 1, 2004
 * @version   $Revision$
 */
public abstract class AbstractProfileIndexingAction extends BaseIndexAction {

    private Integer _profileId;

    private ProfileManager _profileManager;


    /**
     * Constructor for the IndexProfileAction object
     *
     * @param factory  Description of Parameter
     * @param indexer  Description of Parameter
     * @param manager  Description of Parameter
     */
    public AbstractProfileIndexingAction(DocumentFactory factory, DocumentIndexer indexer, ProfileManager manager) {
        super(factory, indexer);
        setProfileManager(manager);
    }


    /**
     * Gets the ProfileManager attribute of the IndexProfileAction object
     *
     * @return   The ProfileManager value
     */
    public ProfileManager getProfileManager() {
        return _profileManager;
    }


    /**
     * Gets the ProfileId attribute of the IndexProfileAction object
     *
     * @return   The ProfileId value
     */
    public Integer getProfileId() {
        return _profileId;
    }


    /**
     * Sets the ProfileManager attribute of the IndexProfileAction object
     *
     * @param profileManager  The new ProfileManager value
     */
    public void setProfileManager(ProfileManager profileManager) {
        _profileManager = profileManager;
    }


    /**
     * Sets the ProfileId attribute of the IndexProfileAction object
     *
     * @param profileId  The new ProfileId value
     */
    public void setProfileId(Integer profileId) {
        _profileId = profileId;
    }


    /**
     * Gets the Profile attribute of the IndexProfileAction object
     *
     * @return               The Profile value
     * @exception Exception  Description of Exception
     */
    Profile getProfile() throws Exception {
        return getProfileManager().getProfile(getProfileId());
    }

}
