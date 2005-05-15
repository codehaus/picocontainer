/**
 * @author Paul Hammant
 * @version $Revision$
 */

// START SNIPPET: class
package cdibook.patterns.helloworld.impl.remote;

import cdibook.patterns.helloworld.HelloWorld;
public class RemoteHelloWorld implements HelloWorld {
    private RemoteMessenger remoteMessenger;
    public RemoteHelloWorld(RemoteMessenger remoteMessenger) {
        this.remoteMessenger = remoteMessenger;
    }
    public void sayHello(String greeting) {
        remoteMessenger.sendMessage("HelloWorld Greeting: " + greeting);
    }
}
// END SNIPPET: class

// fake component
class RemoteMessenger {
    public void sendMessage(String s) {}
}
