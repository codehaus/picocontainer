package org.nanocontainer.type3msg;

public class TestGirl implements Kissable {
    private boolean kissed = false;

    public TestGirl() {
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
