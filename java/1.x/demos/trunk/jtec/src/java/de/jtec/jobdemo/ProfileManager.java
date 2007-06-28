/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo;

import de.jtec.jobdemo.beans.Profile;
import java.io.Serializable;
import java.util.Collection;

/**
 * interface for profile manager
 *
 * @author    kostik
 * @created   November 22, 2004
 * @version   $Revision$
 */
public interface ProfileManager {
    /**
     * retrieve profile based by ID
     *
     * @param id                      id of profile to be retrieved
     * @return                        person profile
     * @exception StructureException  will b e thrown on internal problems
     */
    Profile getProfile(Serializable id) throws StructureException;


    /**
     * load profile by id as string ( usefull for templates / actions )
     *
     * @param id                      Description of Parameter
     * @return                        person profile corresponding to ID or null
     * @exception StructureException  Description of Exception
     */
    Profile loadProfile(String id) throws StructureException;


    /**
     * create new profile.
     *
     * @param profile                 configured profile bean
     * @return                        persisted profile bean
     * @exception StructureException  will be thrown on internal exception 
     * @exception SecurityException   will be thrown if caller is not allowed todo this. 
     */
    Profile createProfile(Profile profile) throws StructureException, SecurityException;


    /**
     * remove profile
     *
     * @param profile                 profile to be removed
     * @exception StructureException  will be thrown on internal exception 
     * @exception SecurityException   will be thrown if caller is not allowe dto do this. 
     */
    void removeProfile(Profile profile) throws StructureException, SecurityException;


    /**
     *list all known profiles
     *
     * @return                        collection of profiles
     * @exception StructureException  thrown on internal exceptions
     */
    Collection getProfiles() throws StructureException;


    /**
     * update existing profile
     *
     * @param profile                 persistent profile bean
     * @exception StructureException  thrown on internal problems
     * @exception SecurityException   thrown if caller is not allowed to do this 
     */
    void updateProfile(Profile profile) throws StructureException, SecurityException;
}
