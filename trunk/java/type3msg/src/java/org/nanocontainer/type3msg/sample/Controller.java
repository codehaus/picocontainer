/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.type3msg.sample;

/**
 * Created by IntelliJ IDEA.
 * User: skizz
 * Date: Sep 10, 2003
 * Time: 10:18:15 PM
 * To change this template use Options | File Templates.
 */
public class Controller {

    Worker worker;
    private Database database;

    public Controller(Worker worker, Database database) {
        this.worker = worker;
        this.database = database;
    }

    public void fireOffAUnitOfWork(String name) {
        UnitOfWork task = new UnitOfWork(name);
        worker.work(task);
    }

    public void storeObject(String anObject) {
        database.storeObject(anObject);
    }
}
