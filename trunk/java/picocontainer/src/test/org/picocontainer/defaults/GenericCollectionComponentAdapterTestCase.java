package org.picocontainer.defaults;

import junit.framework.TestCase;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Arrays;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class GenericCollectionComponentAdapterTestCase extends MockObjectTestCase {
    public void testShouldInstantiateArrayOfStrings() {
        GenericCollectionComponentAdapter ca = new GenericCollectionComponentAdapter("x", null, String.class, Array.class);

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

    // todo similar tests for generic collections
}