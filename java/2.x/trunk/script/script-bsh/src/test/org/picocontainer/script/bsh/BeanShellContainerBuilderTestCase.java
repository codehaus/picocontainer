/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/

package org.picocontainer.script.bsh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.containers.ImmutablePicoContainer;
import org.picocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.picocontainer.script.LifecycleMode;
import org.picocontainer.script.TestHelper;
import org.picocontainer.script.bsh.BeanShellContainerBuilder;
import org.picocontainer.script.testmodel.A;

/**
 * @author Aslak Helles&oslash;y
 * @author Michael Rimov
 */
public class BeanShellContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    @Test public void testContainerCanBeBuiltWithParent() {
        Reader script = new StringReader("" +
                "java.util.Map m = new java.util.HashMap();\n" +
                "m.put(\"foo\",\"bar\");" +
                "pico = new org.picocontainer.classname.DefaultClassLoadingPicoContainer(parent);\n" +
                "pico.addComponent((Object) \"hello\", m, new org.picocontainer.Parameter[0]);\n");
        PicoContainer parent = new DefaultPicoContainer();
        parent = new ImmutablePicoContainer(parent);
        BeanShellContainerBuilder beanShellContainerBuilder = new BeanShellContainerBuilder(script, getClass().getClassLoader());
        PicoContainer pico = buildContainer(beanShellContainerBuilder, parent, "SOME_SCOPE");
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
        Object o = pico.getComponent("hello");
        assertTrue(o instanceof Map);
        assertEquals("bar", ((Map<?,?>) o).get("foo"));

    }

    @Test
    public void testWithParentClassPathPropagatesWithToBeanShellInterpreter() throws MalformedURLException {
        Reader script = new StringReader("" +
    		"import org.picocontainer.script.*;\n" +
    		"Class clazz;\n"+
            "try {\n" + 
            "    clazz = getClass(\"TestComp\");\n" +
            "} catch (ClassNotFoundException ex) {\n" +
            "     ClassLoader current = this.getClass().getClassLoader(); \n" +
            "     print(current.toString());\n" +
            "     print(current.getParent().toString());\n" +
            "     print(\"Failed ClassLoading: \");\n" +
            "     ex.printStackTrace();\n" +
            "}\n" +
            "print(clazz); \n" +
            "ClassLoader cl = clazz.getClassLoader();" +
            "pico = new ScriptedBuilder(parent).withLifecycle().withCaching().withClassLoader(cl).build();\n" +
            "pico.addComponent(\"TestComp\", clazz, org.picocontainer.Parameter.ZERO);\n");

        

        File testCompJar = TestHelper.getTestCompJarFile();
        assertTrue("Cannot find TestComp.jar. " + testCompJar.getAbsolutePath() + " Please set testcomp.jar system property before running.", testCompJar.exists());
        //System.err.println("--> " + testCompJar.getAbsolutePath());
        URLClassLoader classLoader = new URLClassLoader(new URL[] {testCompJar.toURI().toURL()}, this.getClass().getClassLoader());
        Class<?> testComp = null;
        PicoContainer parent = new DefaultPicoContainer();

        try {
            testComp = classLoader.loadClass("TestComp");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            fail("Unable to load test component from the jar using a url classloader");
        }

        PicoContainer pico = buildContainer(new BeanShellContainerBuilder(script, classLoader), parent, "SOME_SCOPE");
        assertNotNull(pico);
        Object testCompInstance = pico.getComponent("TestComp");
        assertNotNull(testCompInstance);
        assertEquals(testComp.getName(),testCompInstance.getClass().getName());

    }
    
	@Test public void testAutoStartingContainerBuilderStarts() {
        A.reset();
        Reader script = new StringReader("" +
        		"import org.picocontainer.script.*;\n" +
                "pico = new ScriptedBuilder(parent).withLifecycle().withCaching().build();\n" +
                "pico.addComponent(org.picocontainer.script.testmodel.A.class);\n" +
                "");
        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new BeanShellContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
        assertEquals("<A",A.componentRecorder);		
        A.reset();
	}
	
	@Test public void testNonAutoStartingContainerBuildDoesntAutostart() {
        A.reset();
        Reader script = new StringReader("" +
        		"import org.picocontainer.script.*;\n" +
                "pico = new ScriptedBuilder(parent).withLifecycle().withCaching().build();\n" +
                "pico.addComponent(org.picocontainer.script.testmodel.A.class);\n" +
                "");
        PicoContainer parent = new DefaultPicoContainer();
        BeanShellContainerBuilder containerBuilder = new BeanShellContainerBuilder(script, getClass().getClassLoader(), LifecycleMode.NO_LIFECYCLE);
        PicoContainer pico = buildContainer(containerBuilder, parent, "SOME_SCOPE");
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
        assertEquals("",A.componentRecorder);
        A.reset();
    }
    

}
