package org.picocontainer.doc.tutorial.interfaces;

// START SNIPPET: boy

public class Boy implements Kissable {
    public void kiss(Object kisser) {
        System.out.println("I was kissed by " + kisser);
    }
}

// END SNIPPET: boy
