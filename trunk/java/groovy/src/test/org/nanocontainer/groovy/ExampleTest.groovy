package org.picoextras.groovy

import org.picocontainer.defaults.NoSatisfiableConstructorsException
import org.picocontainer.extras.ImplementationHidingComponentAdapterFactory
import org.picocontainer.extras.DefaultLifecyclePicoAdapter

import org.picoextras.testmodel.DefaultWebServerConfig
import org.picoextras.testmodel.WebServer
import org.picoextras.testmodel.WebServerConfig
import org.picoextras.testmodel.WebServerConfigBean
import org.picoextras.testmodel.WebServerImpl

class ExampleTest extends GroovyTestCase {
    
    void testInstantiateBasicCopmonent() {
        builder = new PicoBuilder()
        pico = builder.container {
            component(Xxx$A)
        }

        startAndStop(pico)

        assertEquals("Should match the expression", "<A!A", Xxx.componentRecorder)
    }

    void testInstantiateWithChildContainer() {

        // A and C have no no dependancies. B Depends on A.
        
        builder = new PicoBuilder()
        pico = builder.container {
            component(Xxx$A)
            container() {
                component(Xxx$B)
            }
            component(Xxx$C)
        }

        startAndStop(pico)

        System.out.println("Found: " + Xxx.componentRecorder);
        
        // TODO this method seems non-deterministic, returning either of the following
        //  
        //assertEquals("Should match the expression", "<A!A<C<A!A!C", Xxx.componentRecorder)
        //assertEquals("Should match the expression", "<A!A<A<C!C!A", Xxx.componentRecorder)
    }

    void testInstantiateWithImpossibleComponentDependanciesConsideringTheHierarchy() {

        // A and C have no no dependancies. B Depends on A.

        try {
            builder = new PicoBuilder()
            pico = builder.container {
                component(Xxx$B)
                container() {
                    component(Xxx$A)
                }
                component(Xxx$C)
            }
    
            startAndStop(pico)

            fail("Should not have been able to instansiate component tree due to visibility/parent reasons.")
        } 
        catch (NoSatisfiableConstructorsException e) {
        }
    }

    void testInstantiateWithBespokeComponentAdaptor() {

        builder = new PicoBuilder()
        pico = builder.container(adapterFactory:new ImplementationHidingComponentAdapterFactory()) {
            component(key:WebServerConfig, componentClass:DefaultWebServerConfig)
            component(key:WebServer, componentClass:WebServerImpl)
        }

        startAndStop(pico)

        Object ws = pico.getComponentInstance(WebServer)

        assert ws instanceof WebServer
        //assertFalse(ws instanceof WebServerImpl)
        
        ws = pico.getComponentInstances().get(1)

        assert ws instanceof WebServer

		/*
        //TODO - should be assertFalse( ), we're implementation hiding here !
        assertTrue(ws instanceof WebServerImpl)
        */
        //assertFalse(ws instanceof WebServerImpl)
    }

    void testInstantiateWithInlineConfiguration() {

        builder = new PicoBuilder()
        pico = builder.container {
            bean(beanClass:WebServerConfigBean, host:'foobar.com', port:4321)
            component(key:WebServer, componentClass:WebServerImpl)
        }

        startAndStop(pico)

        
        assertEquals("WebServerConfigBean and WebServerImpl expected", 2, pico.getComponentInstances().size())

        wsc = pico.getComponentInstance(WebServerConfig)
        assertEquals("foobar.com", wsc.getHost())
        assertEquals(4321, wsc.getPort())
    }
    
    protected void startAndStop(pico) {
        adapter = new DefaultLifecyclePicoAdapter(pico)
        adapter.start()
        adapter.dispose()
    }
}