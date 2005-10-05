/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.beans;

import java.util.Map;
import java.util.HashMap;
/**
 * represents specialist profile
 *
 * @author            kostik
 * @created           November 20, 2004
 * @version           $Revision$
 * @hibernate.class
 * @hibernate.query   name="allByLogin" query="from de.jtec.jobdemo.beans.Profile p order by p.lastName , p.firstName"
 */
public class Profile extends BaseEntity {

    /**
     * default serial version UID
     */
    private static final long serialVersionUID = 1L;
    private String  _firstName;
    private String  _lastName;
    private String  _profile;

    private Map     _requests = new HashMap();


    /**
     * map of requests keyed by project
     *
     * @return                         map containing requests
     * @hibernate.map                  inverse="true" cascade="delete"
     *      lazy="true" order-by="dateMillis" table="Request"
     * @hibernate.key                  column="profile"
     * @hibernate.index-many-to-many   class="de.jtec.jobdemo.beans.Project"
     *      column="project"
     * @hibernate.one-to-many          class="de.jtec.jobdemo.beans.Request"
     */
    public Map getRequests() {
        return _requests;
    }


    /**
     * provide first name of person
     *
     * @return               first name of person
     * @hibernate.property
     */
    public String getFirstName() {
        return _firstName;
    }


    /**
     * provide last name of person
     *
     * @return               last name of person
     * @hibernate.property
     */
    public String getLastName() {
        return _lastName;
    }



    /**
     * obtain profile text
     *
     * @return               profile text
     * @hibernate.property
     */
    public String getProfile() {
        return _profile;
    }



    /**
     * Sets the Requests attribute of the Profile object
     *
     * @param requests  The new Requests value
     */
    public void setRequests(Map requests) {
        _requests = requests;
    }



    /**
     * set first name of person
     *
     * @param firstName  first name of person
     */
    public void setFirstName(String firstName) {
        _firstName = firstName;
    }


    /**
     * set last name of person
     *
     * @param lastName  last name of person
     */
    public void setLastName(String lastName) {
        _lastName = lastName;
    }


    /**
     * set profile text
     *
     * @param profile  profile test
     */
    public void setProfile(String profile) {
        _profile = profile;
    }

}
