package picocontainer;

import junit.framework.TestCase;
import picocontainer.defaults.PicoInvocationTargetInitializationException;

public class PicoInvocationTargetInitializationExceptionTestCase extends TestCase
{
    public void testInstantiation()
    {
        try
        {
            new PicoInvocationTargetInitializationException(null);
            fail("Should have barfed");
        }
        catch (IllegalArgumentException iae)
        {
            // expected
        }
    }
}
