package picocontainer;

import junit.framework.TestCase;

public class FailingTestCase extRends TestCase {

    public void testFailure() {
        fail("We should get mail about this");
    }
}
