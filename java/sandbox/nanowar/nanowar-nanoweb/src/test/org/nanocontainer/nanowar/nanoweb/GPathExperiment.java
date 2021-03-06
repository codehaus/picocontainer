package org.nanocontainer.nanowar.nanoweb;

import static org.junit.Assert.assertSame;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.Reference;

import java.beans.IntrospectionException;

import org.junit.Test;

/**
 * See http://jira.codehaus.org/secure/ViewIssue.jspa?id=14385
 * @author Aslak Helles&oslash;y
 */
public class GPathExperiment {
    public static class Root {
        private NodeOne nodeOne;

        public NodeOne getNodeOne() {
            return nodeOne;
        }

        public void setNodeOne(NodeOne nodeOne) {
            this.nodeOne = nodeOne;
        }
    }

    public static class NodeOne {
        private NodeTwo nodeTwo;

        public NodeTwo getNodeTwo() {
            return nodeTwo;
        }

        public void setNodeTwo(NodeTwo nodeTwo) {
            this.nodeTwo = nodeTwo;
        }
    }

    public static class NodeTwo {
        private int val;

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }
    }

    @Test public void testShouldSetPropertiesWithGPath() throws IntrospectionException {
        Root root = new Root();
        GroovyObjectSupport rootSupport = new Reference(root);

        // Both Groovy and OGNL can do this
        NodeOne nodeOne = new NodeOne();
        rootSupport.setProperty("nodeOne", nodeOne);
        assertSame(nodeOne, root.getNodeOne());

        // OGNL can do this, but Groovy can't :-(
//        NodeTwo nodeTwo = new NodeTwo();
//        rootSupport.setProperty("nodeOne.nodeTwo", nodeTwo);
//        assertSame(nodeTwo, nodeOne.getNodeTwo());
    }

}