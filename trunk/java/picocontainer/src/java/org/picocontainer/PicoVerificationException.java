package org.picocontainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoVerificationException extends PicoException {
    private List nestedExceptions = new ArrayList();

    public PicoVerificationException(List nestedExceptions) {
        this.nestedExceptions = nestedExceptions;
    }

    public List getNestedExceptions() {
        return nestedExceptions;
    }

}
