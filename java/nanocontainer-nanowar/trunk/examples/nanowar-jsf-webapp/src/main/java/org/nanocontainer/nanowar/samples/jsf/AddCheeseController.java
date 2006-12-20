/*****************************************************************************
 * Copyright (c) 2003-2006 Centerline Computers,  All rights reserved.       *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the          *
 * Centerline Binary Code License Agreement,  a copy of which has been       *
 * included with this distribution in the LICENSE.txt file.                  *
 *                                                                           *
 * Original code by Centerline Computers, Inc.                               *
 *****************************************************************************/
package org.nanocontainer.nanowar.samples.jsf;

import java.io.Serializable;
import org.nanocontainer.nanowar.sample.model.Cheese;
import org.nanocontainer.nanowar.sample.service.CheeseService;

/**
 * AddCheeseController
 * @author Michael Rimov
 * @owner Centerline Computers, Inc.
 */
public class AddCheeseController implements Serializable {
    /**
     * Serial UUID.
     */
    private static final long serialVersionUID = 1L;

    
    private String name;
    
    private String country;

    private final CheeseService service;
    
    
    public AddCheeseController(CheeseService service) {
       this.service = service;
    }
    
    
    public String addCheese() {
        Cheese cheese = new Cheese(name,country);
        service.save(cheese);
        return "addCheeseSuccess";        
    }


    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }


    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    
  
    
}
