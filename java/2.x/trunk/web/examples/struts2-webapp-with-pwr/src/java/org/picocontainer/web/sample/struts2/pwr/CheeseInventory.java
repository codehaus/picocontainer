/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.web.sample.struts2.pwr;

import org.picocontainer.web.sample.service.CheeseService;
import org.picocontainer.web.sample.model.Brand;

import com.opensymphony.xwork2.Action;

import java.util.List;
import java.util.ArrayList;

/**
 * Example of a XWork action that relies on constructor injection.
 * 
 * @author Paul Hammant
 */
public class CheeseInventory implements Action {

    private final CheeseService cheeseService;
    private List cheeses;
    private Brand brand;

    public CheeseInventory(CheeseService cheeseService, Brand brand) {
        this.cheeseService = cheeseService;
        this.brand = brand;
    }

    public List getCheeses() {
        return cheeses;
    }

    public String getBrand() {
        return "Brand:" + brand.getName();
    }

    public String execute() throws Exception {
        cheeses = new ArrayList(cheeseService.getCheeses());
        return SUCCESS;
    }

    public int ajaxCheeseCount() throws Exception {
        // Struts2 actions need execute to be called before they are OK
        // this method is being called outside of Struts
        execute();
        return cheeses.size();
    }

}


