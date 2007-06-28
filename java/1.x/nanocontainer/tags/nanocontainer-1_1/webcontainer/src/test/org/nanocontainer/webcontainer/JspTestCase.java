package org.nanocontainer.webcontainer;

import junit.framework.TestCase;
import org.mortbay.util.IO;
import org.mortbay.jetty.handler.ErrorHandler;

import org.picocontainer.alternatives.EmptyPicoContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.net.Socket;

public class JspTestCase extends TestCase {

    PicoJettyServer server;

    protected void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
        Thread.sleep(1 * 1000);
    }


    public void testCanInstantiateWebContainerContextAndSimpleJspPage() throws InterruptedException, IOException {

        File warFile = TestHelper.getTestWarFile();

        server = new PicoJettyServer("localhost", 8080, new EmptyPicoContainer());


        //server.addRequestLog(new NCSARequestLog("./logs/jetty-yyyy-mm-dd.log"));


        PicoContextHandler barContext = server.createContext("/bar", true);
        String absolutePath = warFile.getParentFile().getAbsolutePath();
        String scratchDir = warFile.getParentFile().getParentFile().getParentFile().getAbsolutePath() + File.separator + "target" + File.separator + "work";
        new File(scratchDir).mkdirs();
        barContext.setDefaultHandling(absolutePath + "/", scratchDir, "*.jsp");

        server.start();

        Thread.sleep(2 * 1000);

        URL url = new URL("http://localhost:8080/bar/test.jsp");
        assertEquals("<HTML>\n" +
                "  <HEAD>\n" +
                "    <TITLE>Test JSP</TITLE>\n" +
                "  </HEAD>\n" +
                "  <BODY>\n" +
                "    hello\n" +
                "  </BODY>\n" +
                "</HTML>", IO.toString(url.openStream()));

        Thread.sleep(1 * 1000);


    }

    public void testCanInstantiateWebContainerContextAndMissingJspPageHandled() throws InterruptedException, IOException {

        File warFile = TestHelper.getTestWarFile();

        server = new PicoJettyServer("localhost", 8080, new EmptyPicoContainer());

        PicoContextHandler barContext = server.createContext("/bar", true);
        barContext.addErrorHandler(new MyErrorHandler());
        String absolutePath = warFile.getParentFile().getAbsolutePath();
        String scratchDir = warFile.getParentFile().getParentFile().getParentFile().getAbsolutePath() + File.separator + "target" + File.separator + "work";
        new File(scratchDir).mkdirs();
        barContext.setDefaultHandling(absolutePath + "/", scratchDir, "*.jsp");

        server.start();

        Thread.sleep(2 * 1000);


        Socket socket = new Socket("localhost", 8080);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        writer.write("GET /bar/barfs.jsp HTTP/1.0\n\n\n");
        writer.flush();
        LineNumberReader lnr = new LineNumberReader(new InputStreamReader(socket.getInputStream()));
        String line = lnr.readLine();
        String result = "";
        while(line != null) {
            result = result + line + "\n";
            line = lnr.readLine();
        }

        assertTrue(result.indexOf("Banzai") != -1);
        assertTrue(result.indexOf("HTTP/1.1 500") != -1);

        Thread.sleep(1 * 1000);


    }
    private static class MyErrorHandler extends ErrorHandler {
        protected void handleErrorPage(HttpServletRequest request, Writer writer, int code, String message)
            throws IOException
        {
            writer.write("<br>Banzai!<br><br>");
            super.handleErrorPage(request, writer, code, message);
        }

    }

}
