package picocontainer;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.sql.SQLException;

public class ReflectionUsingLifecycleManagerTestCase extends TestCase {

    public void testBasic() {

        ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

        final ArrayList messages = new ArrayList();

        try {
            lm.startComponent(new Object() {
                public void start() {
                    messages.add("started");
                }
            });
        } catch (PicoStartException e) {
            fail("Should have started just fine");
        }

        assertEquals(1,messages.size());
        assertEquals("started",messages.get(0));
    }

    public void ttestFailing() throws PicoStartException {

        ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

        final ArrayList messages = new ArrayList();

        try {
            lm.startComponent(new Object() {
                public void start() throws SQLException {
                    throw new SQLException();
                }
            });
            fail("Should have barfed");
        } catch (PicoInvocationTargetStartException e) {
            // expected
            e.printStackTrace();
        }
    }
}
