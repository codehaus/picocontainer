/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.sample.nanowar.webwork;

import org.nanocontainer.sample.nanowar.model.Cheese;
import org.nanocontainer.sample.nanowar.service.CheeseService;
import org.nanocontainer.sample.nanowar.service.defaults.DefaultCheeseService;
import org.nanocontainer.sample.nanowar.dao.simple.MemoryCheeseDao;
import webwork.action.ActionSupport;
import webwork.action.CommandDriven;
import junit.framework.TestCase;
/**
 * Simple test case to demonstrate webwork action testing
 * @author Konstantin Pribluda
 *
 */
// SNIPPET START: testcase
public class CheeseActionTest extends TestCase {
    
    /**
    * test that cheese action works and store cheese in service
    */
    public void testCheeseAction() throws Exception {
        DefaultCheeseService service = new DefaultCheeseService(new MemoryCheeseDao());
        
        CheeseAction action = new CheeseAction(service);
        action.getCheese().setName("gouda");
        action.getCheese().setCountry("Netherlands");
        action.setCommand("save");
        assertEquals(action.SUCCESS,action.execute());
        
        assertEquals(1, service.getCheeses().size());
        
        action.setCommand("remove");
        assertEquals(action.SUCCESS,action.execute());
        
        assertEquals(0, service.getCheeses().size());
        
    }
}
// SNIPPET END: testcase

