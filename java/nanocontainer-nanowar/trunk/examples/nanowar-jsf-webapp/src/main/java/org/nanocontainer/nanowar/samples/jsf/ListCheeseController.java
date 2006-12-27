/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           * 
 * Original code by Centerline Computers, Inc.                               *
 *****************************************************************************/

package org.nanocontainer.nanowar.samples.jsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import org.nanocontainer.nanowar.sample.model.Cheese;
import org.nanocontainer.nanowar.sample.service.CheeseService;

/**
 * ListCheeseController
 * @author Michael Rimov
 */
public class ListCheeseController implements Serializable {
    
    private final CheeseService service;
    
    private List cheeses;
    
    private UIData cheeseList;
    
    
    public ListCheeseController(CheeseService service) {
       this.service = service;       
    }
    
    public List getCheeses() {
        if (cheeses == null) {
            cheeses = new ArrayList(service.getCheeses());
        }
        return cheeses;
    }
    
    
    public int getNumCheeses() {
        return cheeses.size();
    }

    /**
     * @return the cheeseList
     */
    public UIData getCheeseList() {
        return cheeseList;
    }

    /**
     * @param cheeseList the cheeseList to set
     */
    public void setCheeseList(UIData cheeseList) {
        this.cheeseList = cheeseList;
    }
    
    private String getSelectedCheeseName() {
        Cheese project = (Cheese) cheeseList.getRowData(); //  make sure it still exists
        if (project == null) {
            throw new NullPointerException("Project");
        }
        
        return project.getName();
    }
    
    public String removeCheese() {
        Cheese tempCheese = new Cheese(getSelectedCheeseName(), "");
        Cheese storedCheese = service.find(tempCheese);
        if (storedCheese == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cheese " + tempCheese.getName() + "  Not Found!"));
            return "delete error";
        } 
        
        service.remove(storedCheese);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cheese Removed"));
        return "ok";        
    }
}
