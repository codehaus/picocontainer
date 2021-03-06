/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved. 
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.remoting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.web.NONE;
import static org.picocontainer.web.remoting.JsonPicoWebRemotingServlet.makeJsonDriver;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonWriter;

/**
 * @author Paul Hammant
 */
public final class PicoWebRemotingTestCase {

    private XStream xStream = new XStream(makeJsonDriver(JsonWriter.DROP_ROOT_MODE));
    PicoWebRemotingMonitor monitor = new NullPicoWebRemotingMonitor();

    @Test
    public void testPaths() throws Exception {

        PicoWebRemoting pwr = new PicoWebRemoting(xStream, "alpha/", null, "y", false);

        pwr.directorize("foo/bar/baz1");
        pwr.directorize("foo/bar/baz2");

        assertEquals(3, pwr.getPaths().size());
        assertTrue(pwr.getPaths().get("foo") instanceof PicoWebRemoting.Directories);

        PicoWebRemoting.Directories dirs = (PicoWebRemoting.Directories) pwr.getPaths().get("foo");
        assertEquals(1, dirs.size());
        assertEquals("bar", dirs.toArray()[0]);

        dirs = (PicoWebRemoting.Directories) pwr.getPaths().get("foo/bar");
        List<String> sorted = sortedListOf(dirs);
        assertEquals(2, sorted.size());
        assertEquals("baz1", sorted.get(0));
        assertEquals("baz2", sorted.get(1));

        dirs = (PicoWebRemoting.Directories) pwr.getPaths().get("");
        assertEquals(1, dirs.size());
        assertEquals("foo", dirs.toArray()[0]);
    }

    private static List<String> sortedListOf(PicoWebRemoting.Directories dirs) {
        List<String> list = new ArrayList<String>(dirs);
        Collections.sort(list);
        return list;
    }

    @Test
    public void testMissingMethodWillCauseAMethodList() throws Exception {
        PicoWebRemoting pwr = new PicoWebRemoting(xStream, "alpha/", null, "y", false);
        pwr.directorize("alpha/beta", Foo.class);

        DefaultPicoContainer pico = new DefaultPicoContainer();

        String result = pwr.processRequest("/beta/", pico, "GET");
        assertEquals(
                "[\n" +
                "  \"hello\",\n" +
                "  \"goodbye\"\n" +
                "]\n", result);
    }

    @Test
    public void testMissingParamWillCauseASuitableMessage() throws Exception {
        PicoWebRemoting pwr = new PicoWebRemoting(xStream, "alpha/", null, "y", false);
        pwr.setMonitor(new NullPicoWebRemotingMonitor());
        pwr.directorize("alpha/Foo", Foo.class);

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Foo.class);

        String result = pwr.processRequest("/Foo/hello", pico, "GET");
        assertEquals(
                "{\n" +
                "  \"ERROR\": true,\n" +
                "  \"message\": \"Parameter 'longArg' missing\"\n" +
                "}\n", result);
    }

    @Test
    public void testRightParamWillCauseInvocation() throws Exception {
        PicoWebRemoting pwr = new PicoWebRemoting(xStream, "alpha/", null, "y", false);
        pwr.directorize("alpha/Foo", Foo.class);

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Foo.class);
        pico.addComponent("longArg", new Long(123));

        String result = pwr.processRequest("/Foo/hello", pico, "GET");
        assertEquals("11\n", result);

        result = pwr.processRequest("/Foo/goodbye", pico, "GET");
        assertEquals("33\n", result);
    }

    @Test
    public void testRightParamWillCauseInvocationForCaseInsensitive() throws Exception {
        PicoWebRemoting pwr = new PicoWebRemoting(xStream, "org/picocontainer/web/remoting/", null, "y", true);
        pwr.setMonitor(new NullPicoWebRemotingMonitor());

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Foo.class);
        pwr.publishAdapters(pico.getComponentAdapters(), "y");

        pico.addComponent("longArg", new Long(123));

        String result = pwr.processRequest("/picowebremotingtestcase$foo/hello", pico, "GET");
        assertEquals("11\n", result);

        result = pwr.processRequest("/picowebremotingtestcase$foo/goodbye", pico, "GET");
        assertEquals("33\n", result);
    }

    @Test
    public void testRightParamWillCauseInvocation2() throws Exception {
        PicoWebRemoting pwr = new PicoWebRemoting(xStream, "alpha/", ".ajax", "y", false);
        pwr.directorize("alpha/Foo", Foo.class);

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Foo.class);
        pico.addComponent("longArg", new Long(123));

        String result = pwr.processRequest("/Foo/hello.ajax", pico, "GET");
        assertEquals("11\n", result);

    }

    @Test
    public void testRightParamWillCauseInvocationWithNoPrefix() throws Exception {
        PicoWebRemoting pwr = new PicoWebRemoting(xStream, "", null, "y", false);
        pwr.directorize("Foo", Foo.class);

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Foo.class);
        pico.addComponent("longArg", new Long(123));

        String result = pwr.processRequest("/Foo/hello", pico, "GET");
        assertEquals("11\n", result);

        result = pwr.processRequest("/Foo/goodbye", pico, "GET");
        assertEquals("33\n", result);
    }

    @Test
    public void testHiddenMethodNotPublished() throws Exception {
        PicoWebRemoting pwr = new PicoWebRemoting(xStream, "alpha/", null, "y", false);
        pwr.setMonitor(new NullPicoWebRemotingMonitor());

        pwr.directorize("alpha/Foo", Foo.class);

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Foo.class);

        String result = pwr.processRequest("/Foo/shhh", pico, "GET");
        assertEquals("{\n" +
                "  \"ERROR\": true,\n" +
                "  \"message\": \"Nothing matches the path requested\"\n" +
                "}\n", result);

    }

    @Test
    public void testNonExistantMethodNotPublished() throws Exception {
        PicoWebRemoting pwr = new PicoWebRemoting(xStream, "alpha/", null, "y", false);
        pwr.setMonitor(new NullPicoWebRemotingMonitor());

        pwr.directorize("alpha/Foo", Foo.class);

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Foo.class);

        String result = pwr.processRequest("/Foo/kjhsdfjhsdfjhgasdfadfsdf", pico, "GET");
        assertEquals("{\n" +
                "  \"ERROR\": true,\n" +
                "  \"message\": \"Nothing matches the path requested\"\n" +
                "}\n", result);

    }

    public static class Foo {
        public int hello(long longArg) {
            return 11;
        }
        public int goodbye() {
            return 33;
        }

        @NONE
        public boolean shhh() {
            return true;
        }

    }

}
