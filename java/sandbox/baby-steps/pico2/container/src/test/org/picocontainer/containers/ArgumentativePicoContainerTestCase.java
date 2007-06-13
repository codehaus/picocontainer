package org.picocontainer.containers;

import junit.framework.TestCase;

public class ArgumentativePicoContainerTestCase extends TestCase {

    public void testBasicParsing() {
        ArgumentativePicoContainer apc = new ArgumentativePicoContainer(new String[] {
            "foo=bar", "foo2=12", "foo3=true", "foo4="
        });
        assertEquals("bar",apc.getComponent("foo"));
        assertEquals(12,apc.getComponent("foo2"));
        assertEquals(true,apc.getComponent("foo3"));
        assertEquals(true,apc.getComponent("foo4"));
    }

    public void testParsingWithDiffSeparator() {
        ArgumentativePicoContainer apc = new ArgumentativePicoContainer(":", new String[] {
            "foo:bar", "foo2:12", "foo3:true"
        });
        assertEquals("bar",apc.getComponent("foo"));
        assertEquals(12,apc.getComponent("foo2"));
        assertEquals(true,apc.getComponent("foo3"));
    }

    public void testParsingWithWrongSeparator() {
        ArgumentativePicoContainer apc = new ArgumentativePicoContainer(":", new String[] {
            "foo=bar", "foo2=12", "foo3=true"
        });
        assertEquals(true,apc.getComponent("foo=bar"));
        assertEquals(true,apc.getComponent("foo2=12"));
        assertEquals(true,apc.getComponent("foo3=true"));
    }

}
