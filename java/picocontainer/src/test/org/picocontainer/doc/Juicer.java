package org.picocontainer.doc;

public class Juicer {
    private final Peelable peelable;
    private final Juicer juicer;

    public Juicer(Peelable peelable, Juicer juicer) {
        this.peelable = peelable;
        this.juicer = juicer;
    }
}