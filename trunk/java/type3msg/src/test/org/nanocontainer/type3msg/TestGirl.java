package org.picoextras.typ3msg;

public class TestGirl implements Kissable {
    private boolean kissed = false;

    public TestGirl(int id) {
    }

    public boolean wasKissed() {
        return kissed;
    }

    public void kiss() {
        kissed = true;
    }

    public void wipeFace() {
        kissed = false;
    }
}
