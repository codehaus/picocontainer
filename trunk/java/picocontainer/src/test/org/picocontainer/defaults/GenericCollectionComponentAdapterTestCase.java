package org.picocontainer.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class GenericCollectionComponentAdapterTestCase extends MockObjectTestCase {
    public void testShouldInstantiateArrayOfStrings() {
        GenericCollectionComponentAdapter ca = new GenericCollectionComponentAdapter("x", null, String.class, String[].class);

        Mock containerMock = mock(PicoContainer.class);
        containerMock.expects(once()).
                method("getComponentAdaptersOfType").
                with(eq(String.class)).
                will(returnValue(Arrays.asList(new ComponentAdapter[]{
                    new InstanceComponentAdapter("y", "Hello"),
                    new InstanceComponentAdapter("z", "World")
                })));
        ca.setContainer((PicoContainer) containerMock.proxy());

        List expected = Arrays.asList(new String[]{"Hello", "World"});
        List actual = Arrays.asList((Object[]) ca.getComponentInstance());
        assertEquals(expected, actual);
    }
    
    static public interface Fish {
    }
    static public class Cod implements Fish {
        public String toString() {
            return "Cod";
        }
    }
    static public class Shark implements Fish {
        public String toString() {
            return "Shark";
        }
    }
    static public class Bowl {
        private final Cod[] cods;
        private final Fish[] fishes;

        public Bowl(Cod cods[], Fish fishes[]) {
            this.cods = cods;
            this.fishes = fishes;
        }
    }
    
    public void testNativeArrays() {
        MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Bowl.class);
        mpc.registerComponentImplementation(Cod.class);
        mpc.registerComponentImplementation(Shark.class);
        Bowl bowl = (Bowl)mpc.getComponentInstance(Bowl.class);
        assertEquals(1,bowl.cods.length);
        assertEquals(2,bowl.fishes.length);
    }

    // todo similar tests for generic collections
}