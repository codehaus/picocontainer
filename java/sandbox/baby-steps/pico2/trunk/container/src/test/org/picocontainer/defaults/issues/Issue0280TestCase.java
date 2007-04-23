package org.picocontainer.defaults.issues;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.defaults.DefaultPicoContainer;

import junit.framework.TestCase;

/**
 * Test case for issue http://jira.codehaus.org/browse/PICO-280
 */
public class Issue0280TestCase extends TestCase
{
    public void testShouldFailIfInstantiationInChildContainerFails()
    {
        MutablePicoContainer parent = new DefaultPicoContainer();
        MutablePicoContainer child = new DefaultPicoContainer(parent);

        parent.registerComponent(CommonInterface.class, ParentImplementation.class);
        child.registerComponent(CommonInterface.class, ChildImplementation.class);

        parent.start();
        
        try
        {
            Object result = child.getComponent(CommonInterface.class);
            
            // should never get here
            assertFalse(result.getClass() == ParentImplementation.class);
        }
        catch (Exception e)
        {
            assertTrue(e.getClass() == PicoInitializationException.class);
        }

    }

	public interface CommonInterface
	{
		
	}
	
	public static class ParentImplementation implements CommonInterface
	{
	}

	public static class ChildImplementation implements CommonInterface
	{
		public ChildImplementation()
		{
			throw new PicoInitializationException("Problem during initialization");
		}
	}

}
