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
package org.nanocontainer.sample.struts.dao;

import org.nanocontainer.sample.struts.Cheese;
import org.nanocontainer.sample.struts.Cheese;

import java.util.Set;
import java.util.Collection;

/**
 * @author Stephen Molitor
 */
public interface CheeseDao {

    void save(Cheese cheese);

    Cheese get(String name);

    Collection all();

}
