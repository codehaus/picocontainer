package org.picocontainer.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;

/**
 * @author Mauro Talevi
 * @version $Revision: 2200 $
 */
public class DecoratingComponentAdapterTestCase extends MockObjectTestCase {

    public void testDecoratingComponentAdapterDelegatesToMonitorThatDoesSupportStrategy() {
        DecoratingComponentAdapter dca = new DecoratingComponentAdapter(mockComponentAdapterThatDoesSupportStrategy());
        dca.changeMonitor(mockMonitorWithNoExpectedMethods());
    }
    
    public void testDecoratingComponentAdapterDelegatesToMonitorThatDoesNotSupportStrategy() {
        DecoratingComponentAdapter dca = new DecoratingComponentAdapter(mockComponentAdapterThatDoesNotSupportStrategy());
        dca.changeMonitor(mockMonitorWithNoExpectedMethods());
    }
    
    private ComponentMonitor mockMonitorWithNoExpectedMethods() {
        Mock mock = mock(ComponentMonitor.class);
        return (ComponentMonitor)mock.proxy();
    }

    private ComponentAdapter mockComponentAdapterThatDoesSupportStrategy() {
        Mock mock = mock(ComponentAdapterThatSupportsStrategy.class);
        mock.expects(once()).method("changeMonitor").withAnyArguments();
        return (ComponentAdapter)mock.proxy();
    }

    private ComponentAdapter mockComponentAdapterThatDoesNotSupportStrategy() {
        Mock mock = mock(ComponentAdapter.class);
        return (ComponentAdapter)mock.proxy();
    }
    
    static interface ComponentAdapterThatSupportsStrategy extends ComponentAdapter, ComponentMonitorStrategy {
    }
}
