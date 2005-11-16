/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.sample.model;

import java.io.Serializable;

/**
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
public class Cheese implements Serializable {

    private String name;
    private String country;

    public Cheese() {
        // default constructor used by some frameworks
        System.err.println("--> new Cheese()1");
    }

    public Cheese(String name, String country) {
        this.name = name;
        this.country = country;
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (country == null) {
            throw new NullPointerException("country");
        }
        System.err.println("--> new Cheese()2");
    }

    public String getName() {
        System.err.println("--> getName() " + name);
        return name;

    }

    public void setName( String name ) {
        this.name = name;
        System.err.println("--> setName() " + name);
    }
    
    public String getCountry() {
        System.err.println("--> getCountry() " + country);
        return country;

    }
    
    public void setCountry( String country ) {
        this.country = country;
        System.err.println("--> setCountry() " + country);
    }

    public String toString() {
        return "[Cheese name=" + name + ", country=" + country + "]";
    }

}