/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.hibernate;
import de.jtec.jobdemo.beans.Profile;
import java.util.Collection;
import de.jtec.jobdemo.AbstractTestBase;
/**
 * test capabilities of profile manager
 *
 * @author    kostik
 * @created   November 22, 2004
 * @version   $Revision$
 */
public class HibernateProfileManagerTest extends AbstractTestBase {

    /**
     * Constructor for the HibernateProfileManagerTest object
     *
     * @exception Exception  Description of Exception
     */
    public HibernateProfileManagerTest() throws Exception {
        super();
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception  Description of Exception
     */
    public void setUp() throws Exception {
        super.setUp();
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void testProfileCreation() throws Exception {
        Profile profile = new Profile();
        profile.setFirstName("foo");
        profile.setLastName("bar");
        profile.setProfile("foo bar baz");

        getProfileManager().createProfile(profile);
        assertNotNull(profile.getId());

        flushSession();

        Profile loaded = (Profile) getSession().get(Profile.class, profile.getId());

        assertEquals(profile, loaded);
        assertEquals(profile.getFirstName(), loaded.getFirstName());
        assertEquals(profile.getLastName(), loaded.getLastName());
        assertEquals(profile.getProfile(), loaded.getProfile());

    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void testProfileUpdate() throws Exception {
        Profile profile = new Profile();
        getProfileManager().createProfile(profile);
        flushSession();

        profile = (Profile) getSession().get(Profile.class, profile.getId());
        profile.setFirstName("foo");
        profile.setLastName("bar");
        profile.setProfile("foo bar baz");

        getProfileManager().updateProfile(profile);

        flushSession();

        Profile loaded = (Profile) getSession().get(Profile.class, profile.getId());

        assertEquals(profile, loaded);
        assertEquals(profile.getFirstName(), loaded.getFirstName());
        assertEquals(profile.getLastName(), loaded.getLastName());
        assertEquals(profile.getProfile(), loaded.getProfile());
    }


    /**
     * test that profile loading works by id
     *
     * @exception Exception  Description of Exception
     */
    public void testProfileIdLoading() throws Exception {
        Profile profile = new Profile();
        getProfileManager().createProfile(profile);

        flushSession();

        Profile loaded = getProfileManager().getProfile(profile.getId());
        assertEquals(profile, loaded);
    }



    /**
     * test that profile seletion works and is sorted by login
     *
     * @exception Exception  Description of Exception
     */
    public void testProfileSelection() throws Exception {
        Profile profile = new Profile();

        getProfileManager().createProfile(profile);
        profile = new Profile();
        getProfileManager().createProfile(profile);

        flushSession();

        Collection profiles = getProfileManager().getProfiles();
        assertEquals(2, profiles.size());

    }

}
