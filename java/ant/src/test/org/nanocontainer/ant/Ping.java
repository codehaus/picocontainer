package org.picoextras.ant;

import junit.framework.Assert;

/**
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class Ping {
    private String prop;
    boolean wasExecuted = false;

    public void setSomeprop(String prop) {
        this.prop = prop;
    }

    public void execute() {
        Assert.assertEquals("The property should be set", "HELLO", prop);
        wasExecuted = true;
        // This is only so we can see the output when run from Ant
        System.out.println("Ping executing :-)");
    }
}
