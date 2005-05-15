/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.beans;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * basic entity functionality. provides id and hascode / equality
 *
 * @author    kostik
 * @created   November 20, 2004
 * @version   $Revision$
 */
public abstract class BaseEntity implements Serializable {
    private Integer _hashCode;
    private Integer _id;


    /**
     * obtain unique identificator
     *
     * @return         unique identifier
     * @hibernate.id   generator-class="native" unsaved-value="null"
     */
    public Integer getId() {
        return _id;
    }


    /**
     * set unique identificator
     *
     * @param id  DOCUMENT ME!
     */
    public void setId(Integer id) {
        _id = id;
    }


    /**
     * euqality check. two objects are queal if their hash codes are equal.
     *
     * @param o  DOCUMENT ME!
     * @return   DOCUMENT ME!
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        return hashCode() == o.hashCode();
    }


    /**
     * hash code is lazily generated out of class name and identity.
     *
     * @return   hash code suitable for comparison
     */
    public int hashCode() {
        if (null == _hashCode) {
            _hashCode = new Integer(new HashCodeBuilder(17, 37).append(_id).append(getClass())
                .toHashCode());
        }

        return _hashCode.intValue();
    }
}
