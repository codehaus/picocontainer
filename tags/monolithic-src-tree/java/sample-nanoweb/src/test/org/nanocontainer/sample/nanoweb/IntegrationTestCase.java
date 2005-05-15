package org.nanocontainer.sample.nanoweb;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class IntegrationTestCase extends XMLTestCase {
    static final int PORT = 12345;

    protected void setUp() throws Exception {
        super.setUp();
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.getControlDocumentBuilderFactory().setIgnoringComments(true);
        XMLUnit.getTestDocumentBuilderFactory().setIgnoringComments(true);
    }

    public void testDummy() {

    }

    public void FIXMEtestWithJetty() throws Exception {
        Server server = new Server();
        try {
            SocketListener listener = new SocketListener();
            listener.setPort(PORT);
            server.addListener(listener);

            String webappDir = new File("target/nanocontainer-sample-nanoweb").getAbsolutePath();
            server.addWebApplication("/nanocontainer-sample-nanoweb/*", webappDir);

            server.start();

            URL url = new URL("http://localhost:" + PORT + "/nanocontainer-sample-nanoweb/game/play.nano");
            InputStream expected = getClass().getResourceAsStream("expected.xhtml");

            InputStream actual = url.openStream();

            assertXMLEqual(new InputStreamReader(expected), new InputStreamReader(actual));
        } finally {
            server.stop();
        }
    }
}