package org.picocontainer.groovy

import org.picocontainer.defaults.NoSatisfiableConstructorsException
import org.picocontainer.extras.ImplementationHidingComponentAdapterFactory
import org.picocontainer.extras.DefaultLifecyclePicoAdapter

import org.nanocontainer.testmodel.DefaultWebServerConfig
import org.nanocontainer.testmodel.WebServer
import org.nanocontainer.testmodel.WebServerConfig
import org.nanocontainer.testmodel.WebServerConfigBean
import org.nanocontainer.testmodel.WebServerImpl

class ExampleTest extends GroovyTestCase {
    
    void testInstantiateBasicCopmonent() {
        builder = new PicoBuilder()
        nano = builder.container {
            component(Xxx$A)
        }

        startAndStop(nano)

        assertEquals("Should match the expression", "<A!A", Xxx.componentRecorder)
    }

    void testInstantiateWithChildContainer() {

        // A and C have no no dependancies. B Depends on A.
        
        builder = new PicoBuilder()
        nano = builder.container {
            component(Xxx$A)
            container() {
                component(Xxx$B)
            }
            component(Xxx$C)
        }

        startAndStop(nano)

        assertEquals("Should match the expression", "<A!A<A<C!C!A", Xxx.componentRecorder)
        //assertEquals("Should match the expression", "<A<C<BB>C>A>!B!C!A", Xxx.componentRecorder)
        //assertEquals("Should match the expression", "*A*B+A_started+B_started+B_stopped+A_stopped+B_disposed+A_disposed", MockMonitor.monitorRecorder)

    }

    void testInstantiateWithImpossibleComponentDependanciesConsideringTheHierarchy() {

        // A and C have no no dependancies. B Depends on A.

        try {
            builder = new PicoBuilder()
            nano = builder.container {
                component(Xxx$B)
                container() {
                    component(Xxx$A)
                }
                component(Xxx$C)
            }
    
            startAndStop(nano)

            fail("Should not have been able to instansiate component tree due to visibility/parent reasons.")
        } 
        catch (NoSatisfiableConstructorsException e) {
        }
    }

    void testInstantiateWithBespokeComponentAdaptor() {

        builder = new PicoBuilder()
        nano = builder.container(adapterFactory:new ImplementationHidingComponentAdapterFactory()) {
            component(key:WebServerConfig, componentClass:DefaultWebServerConfig)
            component(key:WebServer, componentClass:WebServerImpl)
        }

        startAndStop(nano)

/** @todo instanceof not yet supported        
        Object ws = nano.getRootContainer().getComponentInstance(WebServer)

        assertTrue(ws instanceof WebServer)
        assertFalse(ws instanceof WebServerImpl)

        ws = nano.getRootContainer().getComponentInstances().get(1)

        assertTrue(ws instanceof WebServer)

        //TODO - should be assertFalse( ), we're implementation hiding here !
        assertTrue(ws instanceof WebServerImpl)
*/        
    }

    void testInstantiateWithInlineConfiguration() {

        builder = new PicoBuilder()
        nano = builder.container {
            bean(beanClass:WebServerConfigBean, host:'foobar.com', port:4321)
            component(key:WebServer, componentClass:WebServerImpl)
        }

        startAndStop(nano)

        
        assertEquals("WebServerConfigBean and WebServerImpl expected", 2, nano.getComponentInstances().size())

        wsc = nano.getComponentInstance(WebServerConfig)
        assertEquals("foobar.com", wsc.getHost())
        assertEquals(4321, wsc.getPort())
    }
    
    protected void startAndStop(pico) {
        adapter = new DefaultLifecyclePicoAdapter(pico)
        adapter.start()
        adapter.dispose()
    }
}