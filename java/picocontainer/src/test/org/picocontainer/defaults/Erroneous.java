package org.picocontainer.defaults;

import java.awt.*;

public class Erroneous {
    public Erroneous() {
        throw new AWTError("ha!");
    }
}
