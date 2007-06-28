/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.sample.webwork;

import org.nanocontainer.nanowar.sample.model.Cheese;
import org.nanocontainer.nanowar.sample.service.CheeseService;

import com.opensymphony.xwork.ActionSupport;

/**
 * Example of a XWork action that relies on constructor injection.
 * 
 * @author Mauro Talevi
 * @version $Revision: 2099 $
 */
public class CheeseXWorkAction extends ActionSupport {

    private final CheeseService cheeseService;
    private Cheese cheese = new Cheese();

    public CheeseXWorkAction(CheeseService cheeseService) {
        this.cheeseService = cheeseService;
    }
    
    public Cheese getCheese() {
        return cheese;
    }
    
    public String execute() throws Exception {
        return doSave();
    }
    
    public String doSave() {
        try {
            cheeseService.save(cheese);
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            addActionError("Couldn't save cheese: " + cheese);
            return ERROR;
        }
    }

    public String doFind() {
        try {
            cheeseService.find(cheese);
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            addActionError("Couldn't find cheese: "+ cheese);
            return ERROR;
        }
    }
    
    
    public String doRemove() throws Exception {
        try {
        cheeseService.remove(cheeseService.find(cheese));
        return SUCCESS;
        } catch(Exception e) {
            e.printStackTrace();
            addActionError("Couldn't remove cheese " + cheese);
            return ERROR;
        }
    }
}


