package org.picocontainer.doc.hotswapping;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
// START SNIPPET: class
public class Husband implements Man {
    public final Woman partner;

    public Husband(Woman partner) {
        this.partner = partner;
    }

    public int getEndurance() {
        return 10;
    }
}
// START SNIPPET: class
