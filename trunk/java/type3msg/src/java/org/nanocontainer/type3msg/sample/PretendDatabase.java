package org.nanocontainer.type3msg.sample;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: skizz
 * Date: Sep 10, 2003
 * Time: 10:31:29 PM
 * To change this template use Options | File Templates.
 */
public class PretendDatabase implements Database {
    private List objects;

    public PretendDatabase(List objects) {
        this.objects = objects;
    }

    public void storeObject(String anObject) {
        objects.add(anObject);
    }
}
