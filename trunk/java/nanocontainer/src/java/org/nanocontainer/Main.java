package org.nanocontainer;

import org.picocontainer.PicoConfigurationException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException, PicoConfigurationException, ParserConfigurationException {
        if (args.length == 0) {
            System.err.println("NanoContainer: Needs a configuation file as a parameter");
            System.exit(10);
        }
        String nanoContainerConfig = args[0];
        if (nanoContainerConfig.endsWith(".js")) {
            NanoContainer nano = new JavaScriptAssemblyNanoContainer(new FileReader(nanoContainerConfig));
            JavaScriptAssemblyNanoContainer.addShutdownHook(nano);
        } else if (nanoContainerConfig.endsWith(".xml")) {
            NanoContainer nano = new XmlAssemblyNanoContainer(new FileReader(nanoContainerConfig));
            XmlAssemblyNanoContainer.addShutdownHook(nano);
        } else {
            System.err.println("NanoContainer: Unknown configuration file suffix, .js or .xml expected");
            System.exit(10);
        }
    }
}    
    

