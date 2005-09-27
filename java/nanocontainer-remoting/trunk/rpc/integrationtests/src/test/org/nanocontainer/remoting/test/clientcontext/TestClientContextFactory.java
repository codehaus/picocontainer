package org.nanocontainer.remoting.test.clientcontext;

import org.nanocontainer.remoting.ClientContext;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;

/**
 * @author Paul Hammant and Rune Johanessen (pairing for part)
 * @version $Revision: 1.1 $
 */

public class TestClientContextFactory implements ServerSideClientContextFactory {
    public TestClientContextFactory() {
    }

    public ClientContext get() {
        return new TestClientContext();
    }

    //return "TestCCF:" + System.identityHashCode(Thread.currentThread());

    public void set(Long session, ClientContext clientContext) {
    }

    public boolean isSet() {
        return false;
    }
}
