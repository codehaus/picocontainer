package org.picoextras.type3msg.sample;

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

    public void fireOffAUnitOfWork(String name)
    {
        UnitOfWork task = new UnitOfWork(name);
        worker.work(task);
    }

    public void storeObject(String anObject) {
        database.storeObject(anObject);
    }
}
