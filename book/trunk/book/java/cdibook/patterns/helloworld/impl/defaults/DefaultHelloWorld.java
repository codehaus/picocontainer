/**
 * @author Paul Hammant
 * @version $Revision$
 */

// START SNIPPET: class
package cdibook.patterns.helloworld.impl.defaults;
import cdibook.patterns.helloworld.HelloWorld;
public class DefaultHelloWorld implements HelloWorld {
    public void sayHello(String greeting) {
        System.out.println("HelloWorld Greeting: " + greeting);
    }
}
// END SNIPPET: class

