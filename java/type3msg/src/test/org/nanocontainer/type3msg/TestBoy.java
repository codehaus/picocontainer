package org.picoextras.typ3msg;

/**
 * Created by IntelliJ IDEA.
 * User: skizz
 * Date: Sep 10, 2003
 * Time: 9:52:20 PM
 * To change this template use Options | File Templates.
 */
public class TestBoy {
    private Kissable kissable;

    public TestBoy(Kissable kissable) {
        this.kissable = kissable;
    }

    public void doKiss() {
        kissable.kiss();
    }
}
