package org.nanocontainer.script.groovy

import org.picocontainer.defaults.ComponentParameter
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
        pico = builder.container() {
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

    void testInstantiateWithBespokeComponentAdaptor() {

        sb = new StringBuffer();

        builder = new NanoGroovyBuilder()
        caf = new TestComponentAdapterFactory(sb)
        pico = builder.container(adapterFactory:caf) {
            component(key:WebServerConfig, class:DefaultWebServerConfig)
            component(key:WebServer, class:WebServerImpl)
        }

        startAndStop(pico)

        assertTrue(sb.toString().indexOf("called") != -1)
    }

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

    void testSoftInstantiateWithChildContainerWithDynamicClassPath() {

        File testCompJar = new File(System.getProperty("testcomp.jar"))
        testCompJar2 = new File(testCompJar.getParent(),"TestComp2.jar")
        compJarPath = testCompJar.getCanonicalPath()
        compJarPath2 = testCompJar2.getCanonicalPath()

        builder = new NanoGroovyBuilder()
        child = null
        parent = builder.container {
            classpathelement(path:compJarPath)
            component(class:StringBuffer)
            component(class:"TestComp")
            child = container() {
                classpathelement(path:compJarPath2)
                component(class:"TestComp2")
            }
        }
        assertTrue(parent.getComponentInstances().size() == 2)
        assertTrue(child.getComponentInstances().size() == 1)
        assertNotNull(child.getComponentInstanceOfType("TestComp2"))

    }


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
        assertEquals("org.nanocontainer.script.groovy.TestContainer",pico.getClass().getName())
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

    void FIXMEtestInstantiateBasicComponentWithDeepNamedTree() {

        Xxx.reset()

        builder = new NanoGroovyBuilder()
        pico = builder.container {
            container(name:"huey") {
                container(name:"duey") {
                    component(key:"Luis", class:Xxx$A)
                }
            }
            component(HashMap.class)
        }

        startAndStop(pico)

        assertEquals("Should match the expression", "<A!A", Xxx.componentRecorder)
        Object o = pico.getComponentInstance("huey/duey/Luis")
        assertNotNull(o)
    }
    
    public void testComponentInstances() {
    
        builder = new NanoGroovyBuilder()
        pico = builder.container {
            component(key:"Louis", instance:"Armstrong")
            component(key:"Duke", instance: "Ellington")
        }
	
        assertEquals("Armstrong", pico.getComponentInstance("Louis"))
        assertEquals("Ellington", pico.getComponentInstance("Duke"))
    }
	
    public void testConstantParameters() {

        builder = new NanoGroovyBuilder()
        pico = builder.container {
            component(key:"cat", class:HasParams, parameters:[ "c", "a", "t" ])
            component(key:"dog", class:HasParams, parameters:[ "d", "o", "g" ])
        }
	
        cat = pico.getComponentInstance("cat");
        dog = pico.getComponentInstance("dog");
        assertEquals("cat", cat.getParams());
        assertEquals("dog", dog.getParams());
    }

    public void testComponentParameters() {

        builder = new NanoGroovyBuilder()
        pico = builder.container {
            component(key:"a", class:Xxx$A)
            component(key:"b", class:Xxx$B, parameters:[ new ComponentParameter("a") ])
        }
	
        a = pico.getComponentInstance("a");
        b = pico.getComponentInstance("b");
        assertSame(a, b.getA())
    }

}