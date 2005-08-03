package org.picocontainer.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentMonitorStrategy;

/**
 * @author Mauro Talevi
 * @version $Revision: 2200 $
 */
public class DelegatingComponentMonitorTestCase extends MockObjectTestCase {

    public void testDelegatingMonitorThrowsExpectionWhenConstructionWithNullDelegate(){
        try {
            new DelegatingComponentMonitor(null);
        } catch (NullPointerException e) {
            assertEquals("NPE", "monitor", e.getMessage());
        }
    }
    
    public void testDelegatingMonitorThrowsExpectionWhenChangingToNullMonitor(){
        DelegatingComponentMonitor dcm = new DelegatingComponentMonitor();
        try {
            dcm.changeMonitor(null);
        } catch (NullPointerException e) {
            assertEquals("NPE", "monitor", e.getMessage());
        }
    }

    public void testDelegatingMonitorReplacedDelegateThatDoesSupportMonitorStrategy() {
        DelegatingComponentMonitor dcm = new DelegatingComponentMonitor(mockMonitorThatSupportsStrategy());
        dcm.changeMonitor(mockMonitorWithNoExpectedMethods());
        dcm.instantiating(null);
    }
    
    private ComponentMonitor mockMonitorWithNoExpectedMethods() {
        Mock mock = mock(ComponentMonitor.class);
        return (ComponentMonitor)mock.proxy();
    }

    private ComponentMonitor mockMonitorThatSupportsStrategy() {
        Mock mock = mock(MonitorThatSupportsStrategy.class);
        mock.expects(once()).method("changeMonitor").withAnyArguments();
        mock.expects(once()).method("instantiating").withAnyArguments();
        return (ComponentMonitor)mock.proxy();
    }
        
    static interface MonitorThatSupportsStrategy extends ComponentMonitor, ComponentMonitorStrategy {
    }
}
