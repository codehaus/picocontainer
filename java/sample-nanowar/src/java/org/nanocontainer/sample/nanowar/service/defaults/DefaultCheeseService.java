/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.sample.nanowar.service.defaults;

import java.util.Collection;

import org.nanocontainer.sample.nanowar.dao.CheeseDao;
import org.nanocontainer.sample.nanowar.model.Cheese;
import org.nanocontainer.sample.nanowar.service.CheeseService;

/**
 * Default implementation of CheeseService. Typically it will be used a proxy to
 * delegate execution of the MVC-framework specific action.
 * 
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
public class DefaultCheeseService implements CheeseService {

    private final CheeseDao dao;

    public DefaultCheeseService(CheeseDao dao) {
        this.dao = dao;
    }

    public Collection getCheeses() {
        return dao.all();
    }

    public Cheese find(Cheese example) {
        return dao.get(example.getName());
    }
    
    public void save(Cheese cheese) {
        dao.save(cheese);
    }
    
    public void remove(Cheese cheese) {
        dao.remove(cheese);
    }
}