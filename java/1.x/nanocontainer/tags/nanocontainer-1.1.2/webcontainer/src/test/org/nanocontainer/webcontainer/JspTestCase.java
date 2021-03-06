package org.nanocontainer.webcontainer;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

import javax.servlet.http.HttpServletRequest;

import org.mortbay.jetty.handler.ErrorHandler;
import org.picocontainer.alternatives.EmptyPicoContainer;

public class JspTestCase extends WebContainerTestCase {

    PicoJettyServer server;

    protected void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }


    public void testCanInstantiateWebContainerContextAndSimpleJspPage() throws InterruptedException, IOException {

        File warFile = TestHelper.getTestWarFile();

        server = new PicoJettyServer("localhost", 8080, new EmptyPicoContainer());


        //server.addRequestLog(new NCSARequestLog("./logs/jetty-yyyy-mm-dd.log"));


        PicoContext barContext = server.createContext("/bar", true);
        String absolutePath = warFile.getParentFile().getAbsolutePath();
        String scratchDir = warFile.getParentFile().getParentFile().getParentFile().getAbsolutePath() + File.separator + "target" + File.separator + "work";
        new File(scratchDir).mkdirs();
        barContext.setDefaultHandling(absolutePath + "/", scratchDir, "*.jsp");

        server.start();

        assertEquals("<HTML>\n" +
                "  <HEAD>\n" +
                "    <TITLE>Test JSP</TITLE>\n" +
                "  </HEAD>\n" +
                "  <BODY>\n" +
                "    hello\n" +
                "  </BODY>\n" +
                "</HTML>", getPage("http://localhost:8080/bar/test.jsp"));



    }

    public void testCanInstantiateWebContainerContextAndMissingJspPageHandled() throws InterruptedException, IOException {

        File warFile = TestHelper.getTestWarFile();

        server = new PicoJettyServer("localhost", 8080, new EmptyPicoContainer());

        PicoContext barContext = server.createContext("/bar", true);
        barContext.addErrorHandler(new MyErrorHandler());
        String absolutePath = warFile.getParentFile().getAbsolutePath();
        String scratchDir = warFile.getParentFile().getParentFile().getParentFile().getAbsolutePath() + File.separator + "target" + File.separator + "work";
        new File(scratchDir).mkdirs();
        barContext.setDefaultHandling(absolutePath + "/", scratchDir, "*.jsp");

        server.start();

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
