package org.nanocontainer.ant;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class Pung {
    public String text;

    public Pung(Ping ping, String text){
        this.text = text;
        ping.setSomeprop("HELLO");
    }
}
