package org.nanocontainer.script.groovy

import org.picocontainer.defaults.UnsatisfiableDependenciesException
import org.picocontainer.defaults.SynchronizedComponentAdapterFactory

import org.nanocontainer.proxytoys.HotSwappingComponentAdapterFactory
import org.nanocontainer.script.groovy.NanoGroovyBuilder
import org.nanocontainer.testmodel.DefaultWebServerConfig
import org.nanocontainer.testmodel.WebServer
import org.nanocontainer.testmodel.WebServerConfig
import org.nanocontainer.testmodel.WebServerConfigBean
import org.nanocontainer.testmodel.WebServerImpl
import java.io.File

class NanoGroovyBuilderTestCase extends GroovyTestCase {

    void testInstantiateBasicComponent() {

        Xxx.reset()

        builder = new NanoGroovyBuilder()
        pico = builder.container {
            component(Xxx$A)
        }

        startAndStop(pico)

        assertEquals("Should match the expression", "<A!A", Xxx.componentRecorder)
    }

    void testInstantiateBasicComponentInDeeperTree() {

        Xxx.reset()

        builder = new NanoGroovyBuilder()
        pico = builder.container {
            container() {
                component(Xxx$A)
            }
        }

        startAndStop(pico)

        assertEquals("Should match the expression", "<A!A", Xxx.componentRecorder)
    }


// This should work! There seems to be some residual artifacts between this test and the one above.
// comment out all tests bar these top two. run the tests. then come back, and comment out the top
// test and run the suite again. all works then weird, when it was this one that failed in the
// first attempt.
    void testInstantiateWithChildContainer() {

        Xxx.reset()

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

//    TODO NANO-90
//    TODO - The CAF is not being passed into the ctor of the SoftPico.
//    See NanoGroovyBuilder.java
//    void testInstantiateWithBespokeComponentAdaptor() {

//        sb = new StringBuffer();

//        builder = new NanoGroovyBuilder()
//        caf = new TestComponentAdapterFactory(sb)
//        pico = builder.container(adapterFactory:caf) {
//            component(key:WebServerConfig, class:DefaultWebServerConfig)
//            component(key:WebServer, class:WebServerImpl)
//        }

//        startAndStop(pico)

//        assertTrue(sb.toString().indexOf("called") != -1)
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

//      TODO NANO-89
//      TODO - this one fails because the classpathelement 'path' attribute is inaccessible
//             to the env that needs it.
//      void testSoftInstantiateWithChildContainerWithDynamicClassPath() {
//
//        File testCompJar = new File(System.getProperty("testcomp.jar"));
//        compJarPath = testCompJar.getCanonicalPath()
//        builder = new NanoGroovyBuilder()
//        pico = builder.container {
//            classpathelement(path:compJarPath)
//            component("TestComp")
//            container() {
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

    void testInstantiateBasicComponentInCustomContainer() {

        Xxx.reset()

        builder = new NanoGroovyBuilder()
        pico = builder.container(class:TestContainer) {
            component(Xxx$A)
        }

        startAndStop(pico)
        assertEquals("Should match the expression", "<A!A", Xxx.componentRecorder)
        //TODO
        //assertEquals("org.nanocontainer.script.groovy.TestContainer",pico.getClass().getName())
    }

    void testInstantiateBasicComponentWithDeepTree() {

       Xxx.reset()

        builder = new NanoGroovyBuilder()
        pico = builder.container {
            container() {
                container() {
                    component(Xxx$A)
                }
            }
            component(HashMap.class)
        }

        startAndStop(pico)

        assertEquals("Should match the expression", "<A!A", Xxx.componentRecorder)
    }


}