package org.picocontainer.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;

/**
 * @author Mauro Talevi
 * @version $Revision: 2200 $
 */
public class DecoratingComponentAdapterTestCase extends MockObjectTestCase {

    public void testDecoratingComponentAdapterDelegatesToMonitorThatDoesSupportStrategy() {
        DecoratingComponentAdapter adapter = new DecoratingComponentAdapter(mockComponentAdapterThatDoesSupportStrategy());
        adapter.changeMonitor(mockMonitorWithNoExpectedMethods());
        assertNotNull(adapter.currentMonitor());
    }
    
    public void testDecoratingComponentAdapterDelegatesToMonitorThatDoesNotSupportStrategy() {
        DecoratingComponentAdapter adapter = new DecoratingComponentAdapter(mockComponentAdapter());
        adapter.changeMonitor(mockMonitorWithNoExpectedMethods());
        try {
            adapter.currentMonitor();
            fail("PicoIntrospectionException expected");
        } catch (PicoIntrospectionException e) {
            assertEquals("No component monitor found in delegate", e.getMessage());
        }
    }
    
    public void testDecoratingComponentAdapterDelegatesLifecycleManagement() {
        DecoratingComponentAdapter adapter = new DecoratingComponentAdapter(mockComponentAdapterThatCanManageLifecycle());
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
        assertNotNull(adapter.currentLifecycleStrategy());
    }

    public void testDecoratingComponentAdapterIgnoresLifecycleManagementIfDelegateDoesNotSupportIt() {
        DecoratingComponentAdapter adapter = new DecoratingComponentAdapter(mockComponentAdapter());
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
        try {
            adapter.currentLifecycleStrategy();
            fail("PicoIntrospectionException expected");
        } catch (PicoIntrospectionException e) {
            assertEquals("No lifecycle strategy found in delegate", e.getMessage());
        }
    }
    
    ComponentMonitor mockMonitorWithNoExpectedMethods() {
        Mock mock = mock(ComponentMonitor.class);
        return (ComponentMonitor)mock.proxy();
    }

    private ComponentAdapter mockComponentAdapterThatDoesSupportStrategy() {
        Mock mock = mock(ComponentAdapterThatSupportsStrategy.class);
        mock.expects(once()).method("changeMonitor").withAnyArguments();
        mock.expects(once()).method("currentMonitor").will(returnValue(mockMonitorWithNoExpectedMethods()));
        return (ComponentAdapter)mock.proxy();
    }

    private ComponentAdapter mockComponentAdapter() {
        Mock mock = mock(ComponentAdapter.class);
        return (ComponentAdapter)mock.proxy();
    }
    
    static interface ComponentAdapterThatSupportsStrategy extends ComponentAdapter, ComponentMonitorStrategy {
    }

    private ComponentAdapter mockComponentAdapterThatCanManageLifecycle() {
        Mock mock = mock(ComponentAdapterThatCanManageLifecycle.class);
        mock.expects(once()).method("start");
        mock.expects(once()).method("stop");
        mock.expects(once()).method("dispose");
        mock.expects(once()).method("currentLifecycleStrategy").will(returnValue(mockLifecycleStrategyWithNoExpectedMethods()));
        return (ComponentAdapter)mock.proxy();
    }

    private LifecycleStrategy mockLifecycleStrategyWithNoExpectedMethods() {
        Mock mock = mock(LifecycleStrategy.class);
        return (LifecycleStrategy)mock.proxy();
    }

    static interface ComponentAdapterThatCanManageLifecycle extends ComponentAdapter, LifecycleManager {
    }
}
