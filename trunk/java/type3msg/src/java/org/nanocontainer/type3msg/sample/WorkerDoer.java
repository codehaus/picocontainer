package org.nanocontainer.type3msg.sample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: skizz
 * Date: Sep 10, 2003
 * Time: 10:15:14 PM
 * To change this template use Options | File Templates.
 */
public class WorkerDoer implements Worker {
    private List doneTasks;

    public WorkerDoer() {
        this.doneTasks = new ArrayList();
    }

    public void work(UnitOfWork task) {
        doneTasks.add(task.getName());
    }

    public Collection getDoneTasks() {
        return doneTasks;
    }
}
