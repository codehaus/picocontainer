package org.nanocontainer.type3msg.sample;

/**
 * Created by IntelliJ IDEA.
 * User: skizz
 * Date: Sep 10, 2003
 * Time: 10:15:03 PM
 * To change this template use Options | File Templates.
 */
public class WorkerLogger implements Worker {
    private String logged;

    public void work(UnitOfWork task) {
        logged = task.getName();
    }

    public String getLogged() {
        return logged;
    }
}
