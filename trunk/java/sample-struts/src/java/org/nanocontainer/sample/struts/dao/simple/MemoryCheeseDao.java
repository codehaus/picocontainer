/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.sample.struts.dao.simple;

import org.nanocontainer.sample.struts.Cheese;
import org.nanocontainer.sample.struts.dao.CheeseDao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Stephen Molitor
 */
public class MemoryCheeseDao implements CheeseDao {

    private final Map cheeses = new HashMap();

    public void save(Cheese cheese) {
        cheeses.put(cheese.getName(), cheese);
    }

    public Cheese get(String name) {
        return (Cheese) cheeses.get(name);
    }

    public Collection all() {
        return cheeses.values();
    }

}
