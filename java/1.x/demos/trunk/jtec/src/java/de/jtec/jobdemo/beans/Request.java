/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.beans;

import java.util.Date;

/**
 * represents certain request for project by professional
 *
 * @author            $author$
 * @created           November 20, 2004
 * @version           $Revision$
 * @hibernate.class
 */
public class Request extends BaseEntity {
    /**
     * default serial version uid
     */
    private static final long serialVersionUID = 1L;
    private Date    _date;
    private Profile _profile;
    private Project _project;

    private long    _dateMillis;


    /**
     * Constructor for the Request object
     */
    public Request() {
    }


    /**
     * create request with current date
     *
     * @param profile  requesting profile
     * @param project  requested project
     */
    public Request(Profile profile, Project project) {
        this(profile, project, new Date());
    }


    /**
     * Creates a new Request object.
     *
     * @param profile  profile in question
     * @param project  project in question
     * @param date     date of request
     */
    public Request(Profile profile, Project project, Date date) {
        setProfile(profile);
        setProject(project);
        setDate(date);
    }


    /**
     * @return               The DateMillis value
     * @hibernate.property
     */
    public long getDateMillis() {
        return _dateMillis;
    }


    /**
     * obtain date of request
     *
     * @return   date of request
     */
    public Date getDate() {
        if (_date == null) {
            _date = new Date(getDateMillis());
        }
        return _date;
    }


    /**
     * obtain profile we belong to
     *
     * @return                  profile we belong to
     * @hibernate.many-to-one   not-null="true" cascade="none" column="profile"
     */
    public Profile getProfile() {
        return _profile;
    }


    /**
     * reference to project in question
     *
     * @return                  project we belong to
     * @hibernate.many-to-one   not-null="true" cascade="none" column="project"
     */
    public Project getProject() {
        return _project;
    }


    /**
     * Sets the DateMillis attribute of the Request object
     *
     * @param dateMillis  The new DateMillis value
     */
    public void setDateMillis(long dateMillis) {
        _dateMillis = dateMillis;
    }


    /**
     * set date of request
     *
     * @param date  date of request
     */
    public void setDate(Date date) {
        _date = date;
        setDateMillis(date.getTime());
    }


    /**
     * set profile we belong to
     *
     * @param profile  profile we belong to
     */
    public void setProfile(Profile profile) {
        _profile = profile;
    }


    /**
     * set project
     *
     * @param project  DOCUMENT ME!
     */
    public void setProject(Project project) {
        _project = project;
    }
}
