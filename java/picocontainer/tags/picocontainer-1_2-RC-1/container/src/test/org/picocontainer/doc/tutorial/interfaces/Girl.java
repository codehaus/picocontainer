package org.picocontainer.doc.tutorial.interfaces;

// START SNIPPET: girl

public class Girl {
    Kissable kissable;

    public Girl(Kissable kissable) {
        this.kissable = kissable;
    }

    public void kissSomeone() {
        kissable.kiss(this);
    }
}

// END SNIPPET: girl
