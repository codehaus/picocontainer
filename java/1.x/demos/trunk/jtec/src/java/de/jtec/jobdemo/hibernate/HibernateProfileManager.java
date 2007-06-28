/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.hibernate;
import de.jtec.jobdemo.beans.Profile;
import de.jtec.jobdemo.ProfileManager;
import de.jtec.jobdemo.StructureException;
import java.io.Serializable;
import java.util.Collection;
import net.sf.hibernate.Session;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;

/**
 * hibernate implementation of profile manager class
 *
 * @author    kostik
 * @created   November 22, 2004
 * @version   $Revision$
 */
public class HibernateProfileManager extends AbstractBaseDao implements ProfileManager {
    /**
     * create profile manager from hibernate session
     *
     * @param session  hibernate session
     */
    public HibernateProfileManager(Session session) {
        super(session);
    }



    /**
     * obtain profile by id
     *
     * @param id                   DOCUMENT ME!
     * @return                     DOCUMENT ME!
     * @throws StructureException  DOCUMENT ME!
     */
    public Profile getProfile(Serializable id) throws StructureException {
        return (Profile) getEntity(id, Profile.class);
    }


    /**
     * obtain collection of profiles
     *
     * @return                     DOCUMENT ME!
     * @throws StructureException  DOCUMENT ME!
     */
    public Collection getProfiles() throws StructureException {
        try {
            Query query = getNamedQuery("allByLogin");
            return query.list();
        } catch (HibernateException he) {
            throw new StructureException(he);
        }
    }


    /**
     * DOCUMENT METHOD
     *
     * @param id                      Description of Parameter
     * @return                        Description of the Returned Value
     * @exception StructureException  Description of Exception
     */
    public Profile loadProfile(String id) throws StructureException {
        return getProfile(Integer.decode(id));
    }


    /**
     * create new profile
     *
     * @param profile              profile data
     * @return                     fresh propfile with ID set
     * @throws StructureException  DOCUMENT ME!
     * @throws SecurityException   DOCUMENT ME!
     */
    public Profile createProfile(Profile profile) throws StructureException, SecurityException {
        createEntity(profile);
        return profile;
    }


    /**
     * DOCUMENT ME!
     *
     * @param profile              DOCUMENT ME!
     * @throws StructureException  DOCUMENT ME!
     * @throws SecurityException   DOCUMENT ME!
     */
    public void removeProfile(Profile profile) throws StructureException, SecurityException {
        removeEntity(profile);
    }


    /**
     * update existing profile
     *
     * @param profile              DOCUMENT ME!
     * @throws StructureException  DOCUMENT ME!
     * @throws SecurityException   DOCUMENT ME!
     */
    public void updateProfile(Profile profile) throws StructureException, SecurityException {
        updateEntity(profile);
    }
}
