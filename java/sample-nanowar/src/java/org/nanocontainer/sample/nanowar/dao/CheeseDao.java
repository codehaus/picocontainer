/**
 * **************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 * *
 * ***************************************************************************
 */
package org.nanocontainer.sample.nanowar.dao;

import java.util.Collection;

import org.nanocontainer.sample.nanowar.model.Cheese;

/**
 * @author Stephen Molitor
 */
public interface CheeseDao {

    void save(Cheese cheese);

    Cheese get(String name);

    Collection all();

}