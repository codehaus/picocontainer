package picocontainer;

import junit.framework.TestCase;

public class AggregatedContainersContainerTestCase extends TestCase {

    public void testBasic() {

        final String acomp = "hello";
        final Integer bcomp = new Integer(123);

        Container a = new Container() {
            public boolean hasComponent(Class compType) {
                return compType == String.class;
            }

            public Object getComponent(Class compType) {
                return compType == String.class ? acomp : null;
            }

            public Object[] getComponents() {
                return new Object[] {acomp};
            }

            public Class[] getComponentTypes() {
                return new Class[] {String.class};
            }
        };

        Container b = new Container() {
            public boolean hasComponent(Class compType) {
                return compType == Integer.class;
            }

            public Object getComponent(Class compType) {
                return compType == Integer.class ? bcomp : null;
            }

            public Object[] getComponents() {
                return new Object[] {bcomp};
            }

            public Class[] getComponentTypes() {
                return new Class[] {Integer.class};
            }
        };

        AggregatedContainersContainer acc = new AggregatedContainersContainer(new Container[] {a, b});

        assertTrue(acc.hasComponent(String.class));
        assertTrue(acc.hasComponent(Integer.class));
        assertTrue(acc.getComponent(String.class) == acomp);
        assertTrue(acc.getComponent(Integer.class) == bcomp);
        assertTrue(acc.getComponents().length == 2);

    }

    public void testEmpty() {

        AggregatedContainersContainer acc = new AggregatedContainersContainer(new Container[0]);
        assertTrue(acc.hasComponent(String.class) == false);
        assertTrue(acc.getComponent(String.class) == null);
        assertTrue(acc.getComponents().length == 0);

    }
}
