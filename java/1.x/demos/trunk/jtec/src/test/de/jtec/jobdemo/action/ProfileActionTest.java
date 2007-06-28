/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import webwork.action.Action;
import de.jtec.jobdemo.AbstractTestBase;
import de.jtec.jobdemo.beans.Profile;

/**
 * test capabilities of profile action
 *
 * @author    kostik
 * @created   November 23, 2004
 * @version   $Revision$
 */
public class ProfileActionTest extends AbstractTestBase {

    /**
     * Constructor for the ProfileActionTest obfile
     *
     * @exception Exception  Description of Exception
     */
    public ProfileActionTest() throws Exception {
        super();
    }


    /**
     * test that action creates profile properly
     *
     * @exception Exception  Description of Exception
     */
    public void testProfileCreation() throws Exception {
        CreateProfileAction action = new CreateProfileAction(getProfileManager());

        assertEquals(Action.SUCCESS, action.execute());

        flushSession();
        assertEquals(1, getProfileManager().getProfiles().size());
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void testProfileLoading() throws Exception {
        Profile profile = new Profile();
        getProfileManager().createProfile(profile);
        flushSession();

        LoadProfileAction action = new LoadProfileAction(getProfileManager());
        action.setProfileId(profile.getId());
        assertEquals(Action.SUCCESS, action.execute());
        assertEquals(profile.getId(), action.getProfile().getId());
    }


    /**
     * test that action removes proejct properly
     *
     * @exception Exception  Description of Exception
     */
    public void testProfileRemove() throws Exception {
        Profile profile = new Profile();
        getProfileManager().createProfile(profile);
        flushSession();

        RemoveProfileAction action = new RemoveProfileAction(getProfileManager());
        action.setProfileId(profile.getId());
        assertEquals(Action.SUCCESS, action.execute());
        flushSession();
        assertNull(getProfileManager().getProfile(profile.getId()));

    }


    /**
     * test action updating profile
     *
     * @exception Exception  Description of Exception
     */
    public void testProfileUpdate() throws Exception {
        Profile profile = new Profile();
        getProfileManager().createProfile(profile);
        flushSession();

        UpdateProfileAction action = new UpdateProfileAction(getProfileManager());
        action.setProfileId(profile.getId());
        action.getProfile().setFirstName("glem");
        assertEquals(Action.SUCCESS, action.execute());
        flushSession();
        assertEquals("glem", getProfileManager().getProfile(profile.getId()).getFirstName());
    }
}
