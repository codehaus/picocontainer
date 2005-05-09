package org.picocontainer.doc.tutorial.lifecycle;

import org.picocontainer.doc.tutorial.interfaces.Kissable;
import org.picocontainer.Startable;

// START SNIPPET: girl

public class Girl implements Startable {
    Kissable kissable;

    public Girl(Kissable kissable) {
        this.kissable = kissable;
    }

    public void start() {
        kissable.kiss(this);
    }

    public void stop() {
    }
}

// END SNIPPET: girl
