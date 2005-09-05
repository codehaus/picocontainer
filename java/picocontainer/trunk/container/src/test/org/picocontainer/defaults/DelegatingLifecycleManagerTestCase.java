/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.defaults;

import java.util.Arrays;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Disposable;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @author Mauro Talevi
 * @version $Revision$
 */
public class DelegatingLifecycleManagerTestCase extends MockObjectTestCase {

    StringBuffer events = new StringBuffer();

    Object[] lifecycleManagers;
    
    class TestComponent implements Startable, Disposable{
        String code;

        public TestComponent(String code) {
            this.code = code;
        }

        public void start() {
            events.append("<" + code);
        }
        public void stop() {
            events.append(code + ">");
        }

        public void dispose() {
            events.append("!" + code);
        }

    }
    protected void setUp() throws Exception {
        InstanceComponentAdapter one = new InstanceComponentAdapter(TestComponent.class, new TestComponent("One"));
        InstanceComponentAdapter two = new InstanceComponentAdapter(TestComponent.class, new TestComponent("Two"));
        InstanceComponentAdapter three = new InstanceComponentAdapter(TestComponent.class, new TestComponent("Three"));
        lifecycleManagers = new Object[] {one,two,three}; 
    }

    public void testLifecycleOrderIsMaintained() {
        DelegatingLifecycleManager dlm = new DelegatingLifecycleManager();
        PicoContainer pico = mockPicoContainer(lifecycleManagers);
        dlm.start(pico);
        dlm.stop(pico);
        dlm.dispose(pico);
        assertEquals("<One<Two<ThreeThree>Two>One>!Three!Two!One", events.toString());
    }

    public void testLifecycleIsIgnoredIfAdaptersAreNotLifecycleManagers() {
        DelegatingLifecycleManager dlm = new DelegatingLifecycleManager();
        PicoContainer pico = mockPicoContainer(new Object[] {mockComponentAdapterThatIsNotALifecycleManager()});
        dlm.start(pico);
        dlm.stop(pico);
        dlm.dispose(pico);
        assertEquals("", events.toString());        
    }

    private ComponentAdapter mockComponentAdapterThatIsNotALifecycleManager() {
        Mock mock = mock(ComponentAdapter.class);
        return (ComponentAdapter) mock.proxy();
    }

    private PicoContainer mockPicoContainer(Object[] adapters) {
        Mock mock = mock(PicoContainer.class);
        mock.expects(atLeastOnce()).method("getComponentAdapters").will(returnValue(Arrays.asList(adapters)));
        return (PicoContainer) mock.proxy();
    }

}
