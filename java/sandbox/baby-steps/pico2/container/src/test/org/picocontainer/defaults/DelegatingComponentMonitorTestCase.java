package org.picocontainer.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.picocontainer.ComponentMonitor;

import java.lang.reflect.Constructor;
import java.util.*;

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

    public void testMonitoringHappensBeforeAndAfterInstantiation() throws NoSuchMethodException {
        final Vector ourIntendedInjectee0 = new Vector();
        final String ourIntendedInjectee1 = "hullo";
        Mock monitor = mock(ComponentMonitor.class);
        Constructor nacotCtor = NeedsACoupleOfThings.class.getConstructors()[0];
        monitor.expects(once()).method("instantiating").with(eq(nacotCtor));
        Constraint durationIsGreaterThanOrEqualToZero = new Constraint() {
            public boolean eval(Object o) {
                Long duration = (Long)o;
                return 0 <= duration.longValue();
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("The endTime wasn't after the startTime");
            }
        };
        Constraint isANACOTThatWozCreated = new Constraint() {
            public boolean eval(Object o) {
                return o instanceof NeedsACoupleOfThings;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("Should have been a hashmap");
            }
        };
        Constraint collectionAndStringWereInjected = new Constraint() {
            public boolean eval(Object o) {
                Object[] ctorArgs = (Object[]) o;
                return ctorArgs.length == 2 && ctorArgs[0] == ourIntendedInjectee0 && ctorArgs[1] == ourIntendedInjectee1;
            }
            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("Should have injected our intended vector and string");
            }
        };
        monitor.expects(once()).method("instantiated").with(eq(nacotCtor), isANACOTThatWozCreated, collectionAndStringWereInjected, durationIsGreaterThanOrEqualToZero);
        DefaultPicoContainer parent = new DefaultPicoContainer();
        parent.registerComponent(ourIntendedInjectee0);
        parent.registerComponent(ourIntendedInjectee1);
        DefaultPicoContainer child = new DefaultPicoContainer(new DelegatingComponentMonitor((ComponentMonitor) monitor.proxy()), parent);
        child.registerComponent(NeedsACoupleOfThings.class);
        child.getComponent(NeedsACoupleOfThings.class);
    }

    public static class NeedsACoupleOfThings {
        public NeedsACoupleOfThings(Collection collection, String string) {
        }
    }

    public static interface MonitorThatSupportsStrategy extends ComponentMonitor, ComponentMonitorStrategy {
    }
}
