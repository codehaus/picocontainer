package org.picocontainer.defaults;

import java.awt.AWTError;

public class Erroneous {
    public Erroneous() {
        throw new AWTError("ha!");
    }
}
