package org.nanocontainer.script.groovy

import org.picocontainer.defaults.UnsatisfiableDependenciesException

import org.nanocontainer.proxytoys.HotSwappingComponentAdapterFactory
import org.nanocontainer.script.groovy.NanoGroovyBuilder
import org.nanocontainer.testmodel.DefaultWebServerConfig
import org.nanocontainer.testmodel.WebServer
import org.nanocontainer.testmodel.WebServerConfig
import org.nanocontainer.testmodel.WebServerConfigBean
import org.nanocontainer.testmodel.WebServerImpl
import java.io.File

class ExampleTest extends GroovyTestCase {

    void testInstantiateBasicComponent() {
        builder = new NanoGroovyBuilder()
        pico = builder.container {
            component(Xxx$A)
        }

        startAndStop(pico)

        assertEquals("Should match the expression", "<A!A", Xxx.componentRecorder)
    }

    void testInstantiateWithChildContainer() {

        // A and C have no no dependancies. B Depends on A.

        builder = new NanoGroovyBuilder()
        pico = builder.container {
            component(Xxx$A)
            container() {
                component(Xxx$B)
            }
            component(Xxx$C)
        }

        startAndStop(pico)

        // TODO this method seems non-deterministic, returning either of the following
        //
        //assertEquals("Should match the expression", "<A!A<C<A!A!C", Xxx.componentRecorder)
        //assertEquals("Should match the expression", "<A!A<A<C!C!A", Xxx.componentRecorder)
    }

    void testInstantiateWithImpossibleComponentDependanciesConsideringTheHierarchy() {

        // A and C have no no dependancies. B Depends on A.

        try {
            builder = new NanoGroovyBuilder()
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
        catch (UnsatisfiableDependenciesException e) {
        }
    }

//    void testInstantiateWithBespokeComponentAdaptor() {
//
//        builder = new NanoGroovyBuilder()
//        pico = builder.container(adapterFactory:new HotSwappingComponentAdapterFactory()) {
//            component(key:WebServerConfig, class:DefaultWebServerConfig)
//            component(key:WebServer, class:WebServerImpl)
//        }
//
//        startAndStop(pico)
//
//        Object ws = pico.getComponentInstance(WebServer)
//
//        assert ws instanceof WebServer
//        assertFalse(ws instanceof WebServerImpl)
//
//        ws = pico.getComponentInstances().get(1)
//
//        assert ws instanceof WebServer
//        assertFalse(ws instanceof WebServerImpl)
//    }

    void testInstantiateWithInlineConfiguration() {

        builder = new NanoGroovyBuilder()
        pico = builder.container {
            bean(beanClass:WebServerConfigBean, host:'foobar.com', port:4321)
            component(key:WebServer, class:WebServerImpl)
        }

        startAndStop(pico)

        assertTrue("WebServerConfigBean and WebServerImpl expected", pico.getComponentInstances().size() == 2)

        wsc = pico.getComponentInstanceOfType(WebServerConfig)
        assertEquals("foobar.com", wsc.getHost())
        assertTrue(wsc.getPort() == 4321)
    }

//    void testSoftInstantiateWithChildContainer() {
//
//        File testCompJar = new File(System.getProperty("testcomp.jar"));
//
//        builder = new NanoGroovyBuilder()
//        pico = builder.softContainer {
//            classpathElement(path:testCompJar.getCanonicalPath())
//            component("TestComp")
//            softContainer() {
//                component("TestComp2")
//            }
//        }
//        comps = pico.getComponentInstances()
//        assertEquals(1, comps.size())
//    }


    protected void startAndStop(pico) {
        pico.start()
        pico.dispose()
    }
}