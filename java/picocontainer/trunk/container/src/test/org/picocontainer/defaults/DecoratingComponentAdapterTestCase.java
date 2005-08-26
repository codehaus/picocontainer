package org.picocontainer.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoIntrospectionException;

/**
 * @author Mauro Talevi
 * @version $Revision: 2200 $
 */
public class DecoratingComponentAdapterTestCase extends MockObjectTestCase {

    public void testDecoratingComponentAdapterDelegatesToMonitorThatDoesSupportStrategy() {
        DecoratingComponentAdapter dca = new DecoratingComponentAdapter(mockComponentAdapterThatDoesSupportStrategy());
        dca.changeMonitor(mockMonitorWithNoExpectedMethods());
        assertNotNull(dca.currentMonitor());
    }
    
    public void testDecoratingComponentAdapterDelegatesToMonitorThatDoesNotSupportStrategy() {
        DecoratingComponentAdapter dca = new DecoratingComponentAdapter(mockComponentAdapterThatDoesNotSupportStrategy());
        dca.changeMonitor(mockMonitorWithNoExpectedMethods());
        try {
            dca.currentMonitor();
            fail("PicoIntrospectionException expected");
        } catch (PicoIntrospectionException e) {
            assertEquals("No component monitor found in delegate", e.getMessage());
        }
    }
    
    private ComponentMonitor mockMonitorWithNoExpectedMethods() {
        Mock mock = mock(ComponentMonitor.class);
        return (ComponentMonitor)mock.proxy();
    }

    private ComponentAdapter mockComponentAdapterThatDoesSupportStrategy() {
        Mock mock = mock(ComponentAdapterThatSupportsStrategy.class);
        mock.expects(once()).method("changeMonitor").withAnyArguments();
        mock.expects(once()).method("currentMonitor").will(returnValue(mockMonitorWithNoExpectedMethods()));
        return (ComponentAdapter)mock.proxy();
    }

    private ComponentAdapter mockComponentAdapterThatDoesNotSupportStrategy() {
        Mock mock = mock(ComponentAdapter.class);
        return (ComponentAdapter)mock.proxy();
    }
    
    static interface ComponentAdapterThatSupportsStrategy extends ComponentAdapter, ComponentMonitorStrategy {
    }
}
