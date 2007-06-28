/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.beans;

import java.util.Map;
import java.util.HashMap;
/**
 * contract for project entity
 *
 * @author            kostik
 * @created           November 20, 2004
 * @version           $Revision$
 * @hibernate.class
 * @hibernate.query   name="allByName" query="from de.jtec.jobdemo.beans.Project p order by p.name"
 */
public class Project extends BaseEntity {
    /**
     * default serial version UID
     */
    private static final long serialVersionUID = 1L;
    private String  _description;
    private String  _name;
    private String  _shortDescription;

    private Map     _requests = new HashMap();


    /**
     * obtain project description
     *
     * @return               project description
     * @hibernate.property
     */
    public String getDescription() {
        return _description;
    }


    /**
     * obtain project name
     *
     * @return               project name
     * @hibernate.property
     */
    public String getName() {
        return _name;
    }


    /**
     * obtain short project description
     *
     * @return               short project description
     * @hibernate.property
     */
    public String getShortDescription() {
        return _shortDescription;
    }


    /**
     * map of requests keyed by project
     *
     * @return                         map containing requests
     * @hibernate.map                  inverse="true" cascade="delete"
     *      lazy="true" order-by="dateMillis" table="Request"
     * @hibernate.key                  column="project"
     * @hibernate.index-many-to-many   class="de.jtec.jobdemo.beans.Profile"
     *      column="profile"
     * @hibernate.one-to-many          class="de.jtec.jobdemo.beans.Request"
     */
    public Map getRequests() {
        return _requests;
    }


    /**
     * set project description
     *
     * @param description  project description
     */
    public void setDescription(String description) {
        _description = description;
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
     * set project name
     *
     * @param name  project name
     */
    public void setName(String name) {
        _name = name;
    }


    /**
     * set short project description
     *
     * @param shortDescription  short project description
     */
    public void setShortDescription(String shortDescription) {
        _shortDescription = shortDescription;
    }
}
