package org.picocontainer.adapters;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.adapters.BehaviorAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ComponentMonitorStrategy;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

/**
 * @author Mauro Talevi
 * @version $Revision: 2200 $
 */
public class BehaviorAdapterTestCase extends MockObjectTestCase {

    public void testDecoratingComponentAdapterDelegatesToMonitorThatDoesSupportStrategy() {
        BehaviorAdapter adapter = new BehaviorAdapter(mockComponentAdapterThatDoesSupportStrategy());
        adapter.changeMonitor(mockMonitorWithNoExpectedMethods());
        assertNotNull(adapter.currentMonitor());
    }
    
    public void testDecoratingComponentAdapterDelegatesToMonitorThatDoesNotSupportStrategy() {
        BehaviorAdapter adapter = new BehaviorAdapter(mockComponentAdapter());
        adapter.changeMonitor(mockMonitorWithNoExpectedMethods());
        try {
            adapter.currentMonitor();
            fail("PicoIntrospectionException expected");
        } catch (PicoIntrospectionException e) {
            assertEquals("No addComponent monitor found in delegate", e.getMessage());
        }
    }
    
    public void testDecoratingComponentAdapterDelegatesLifecycleManagement() {
        BehaviorAdapter adapter = new BehaviorAdapter(mockComponentAdapterThatCanManageLifecycle());
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
        Touchable touchable = new SimpleTouchable();
        adapter.start(touchable);
        adapter.stop(touchable);
        adapter.dispose(touchable);
    }

    public void testDecoratingComponentAdapterIgnoresLifecycleManagementIfDelegateDoesNotSupportIt() {
        BehaviorAdapter adapter = new BehaviorAdapter(mockComponentAdapter());
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
        Touchable touchable = new SimpleTouchable();
        adapter.start(touchable);
        adapter.stop(touchable);
        adapter.dispose(touchable);
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
        mock.expects(once()).method("start").with(isA(PicoContainer.class));
        mock.expects(once()).method("stop").with(isA(PicoContainer.class));
        mock.expects(once()).method("dispose").with(isA(PicoContainer.class));
        mock.expects(once()).method("start").with(isA(Touchable.class));
        mock.expects(once()).method("stop").with(isA(Touchable.class));
        mock.expects(once()).method("dispose").with(isA(Touchable.class));
        return (ComponentAdapter)mock.proxy();
    }

    static interface ComponentAdapterThatCanManageLifecycle extends ComponentAdapter, LifecycleManager, LifecycleStrategy {
    }
}
