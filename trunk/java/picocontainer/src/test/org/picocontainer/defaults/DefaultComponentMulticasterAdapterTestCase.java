/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.lifecycle.Startable;
import org.jmock.Mock;
import org.jmock.C;
import junit.framework.TestCase;

import java.lang.reflect.Method;

public class DefaultComponentMulticasterAdapterTestCase extends TestCase {

    public void testMulticasterInvocationsCanBeInterceptedByInvocationInterceptor() {
        MutablePicoContainer pico = new DefaultPicoContainer();

        Mock mockStartable = new Mock(Startable.class);
        mockStartable.expect("start");
        pico.registerComponentInstance(mockStartable.proxy());

        Mock mockInvocationInterceptor = new Mock(InvocationInterceptor.class);
        mockInvocationInterceptor.expect("intercept", C.args(C.isA(Method.class), C.IS_ANYTHING, C.IS_ANYTHING));

        ComponentMulticasterAdapter cma = new DefaultComponentMulticasterAdapter(new DefaultComponentMulticasterFactory());
        Startable startable = (Startable) cma.getComponentMulticaster(pico, true, (InvocationInterceptor)mockInvocationInterceptor.proxy());

        startable.start();

        mockStartable.verify();
        mockInvocationInterceptor.verify();
    }

}