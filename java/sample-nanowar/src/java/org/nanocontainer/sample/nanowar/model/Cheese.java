/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.sample.nanowar.model;

/**
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
public class Cheese {

    private String name;
    private String country;

    public Cheese(String name, String country) {
        this.name = name;
        this.country = country;
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (country == null) {
            throw new NullPointerException("country");
        }
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String toString() {
        return "[Cheese name=" + getName() + ", country=" + getCountry() + "]";
    }

}