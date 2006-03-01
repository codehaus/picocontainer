package org.picocontainer.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentMonitor;

/**
 * @author Mauro Talevi
 * @version $Revision: 2200 $
 */
public class DelegatingComponentMonitorTestCase extends MockObjectTestCase {

    public void testDelegatingMonitorThrowsExpectionWhenConstructionWithNullDelegate(){
        try {
            new DelegatingComponentMonitor(null);
            fail("NPE expected");
        } catch (NullPointerException e) {
            assertEquals("NPE", "monitor", e.getMessage());
        }
    }
    
    public void testDelegatingMonitorThrowsExpectionWhenChangingToNullMonitor(){
        DelegatingComponentMonitor dcm = new DelegatingComponentMonitor();
        try {
            dcm.changeMonitor(null);
            fail("NPE expected");
        } catch (NullPointerException e) {
            assertEquals("NPE", "monitor", e.getMessage());
        }
    }

    public void testDelegatingMonitorCanChangeMonitorInDelegateThatDoesSupportMonitorStrategy() {
        ComponentMonitor monitor = mockMonitorWithNoExpectedMethods();
        DelegatingComponentMonitor dcm = new DelegatingComponentMonitor(mockMonitorThatSupportsStrategy(monitor));
        dcm.changeMonitor(monitor);
        assertEquals(monitor, dcm.currentMonitor());
        dcm.instantiating(null);
    }

    public void testDelegatingMonitorChangesDelegateThatDoesNotSupportMonitorStrategy() {
        ComponentMonitor delegate = mockMonitorWithNoExpectedMethods();
        DelegatingComponentMonitor dcm = new DelegatingComponentMonitor(delegate);
        ComponentMonitor monitor = mockMonitorWithNoExpectedMethods();
        assertEquals(delegate, dcm.currentMonitor());
        dcm.changeMonitor(monitor);
        assertEquals(monitor, dcm.currentMonitor());
    }
    
    public void testDelegatingMonitorReturnsDelegateThatDoesNotSupportMonitorStrategy() {
        ComponentMonitor delegate = mockMonitorWithNoExpectedMethods();
        DelegatingComponentMonitor dcm = new DelegatingComponentMonitor(delegate);
        assertEquals(delegate, dcm.currentMonitor());
    }
    
    private ComponentMonitor mockMonitorWithNoExpectedMethods() {
        Mock mock = mock(ComponentMonitor.class);
        return (ComponentMonitor)mock.proxy();
    }

    private ComponentMonitor mockMonitorThatSupportsStrategy(ComponentMonitor currentMonitor) {
        Mock mock = mock(MonitorThatSupportsStrategy.class);
        mock.expects(once()).method("changeMonitor").with(eq(currentMonitor));
        mock.expects(once()).method("currentMonitor").withAnyArguments().will(returnValue(currentMonitor));
        mock.expects(once()).method("instantiating").withAnyArguments();
        return (ComponentMonitor)mock.proxy();
    }
        
    static interface MonitorThatSupportsStrategy extends ComponentMonitor, ComponentMonitorStrategy {
    }
}
