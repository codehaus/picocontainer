/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package foo.bar;

import org.picocontainer.Startable;

public class Zap implements Startable {
    private final String hello;
    private String toString = "Not started";

    public Zap(String hello) {
        this.hello = hello;
    }

    public void start() {
        toString = hello + " Started";
    }

    public void stop() {

    }

    public String toString() {
        return toString;
    }
}