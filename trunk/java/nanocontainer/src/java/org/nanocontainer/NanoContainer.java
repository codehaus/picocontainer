package org.nanocontainer;

import org.nanocontainer.xml.InputSourceFrontEnd;
import org.picocontainer.PicoContainer;
import org.picocontainer.extras.DefaultLifecyclePicoAdapter;
import org.picocontainer.lifecycle.LifecyclePicoAdapter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @author Paul Hammant
 * @version $Revision$
 */
public class NanoContainer {
    private final List lifecycleAdapters = new ArrayList();

    public NanoContainer(Reader nanoContainerXml) throws IOException, ParserConfigurationException, ClassNotFoundException, SAXException {
        InputSource is = new InputSource(nanoContainerXml);
        InputSourceFrontEnd isfe = new InputSourceFrontEnd();
        final PicoContainer rootContainer = isfe.createPicoContainer(is);

        instantiateComponentsBreadthFirst(rootContainer);
        startComponentsBreadthFirst();
    }

    private void instantiateComponentsBreadthFirst(PicoContainer picoContainer) {
        LifecyclePicoAdapter lpa = new DefaultLifecyclePicoAdapter(picoContainer);
        lifecycleAdapters.add(lpa);
        picoContainer.getComponentInstances();
        Collection childContainers = picoContainer.getChildContainers();
        for (Iterator iterator = childContainers.iterator(); iterator.hasNext();) {
            PicoContainer childContainer = (PicoContainer) iterator.next();
            instantiateComponentsBreadthFirst(childContainer);
        }
    }

    private void startComponentsBreadthFirst() {
        for (Iterator iterator = lifecycleAdapters.iterator(); iterator.hasNext();) {
            DefaultLifecyclePicoAdapter lpa= (DefaultLifecyclePicoAdapter) iterator.next();
            lpa.start();
        }
        Collections.reverse(lifecycleAdapters); // for stop and dispose
    }

    public void stopComponentsDepthFirst() {
        for (Iterator iterator = lifecycleAdapters.iterator(); iterator.hasNext();) {
            DefaultLifecyclePicoAdapter lpa= (DefaultLifecyclePicoAdapter) iterator.next();
            lpa.stop();
        }
    }

    public void disposeComponentsDepthFirst() {
        for (Iterator iterator = lifecycleAdapters.iterator(); iterator.hasNext();) {
            DefaultLifecyclePicoAdapter lpa= (DefaultLifecyclePicoAdapter) iterator.next();
            lpa.dispose();
        }
    }

    public static void main(String[] args) throws Exception {
        String nanoContainerXml = args[0];
        if (nanoContainerXml == null) {
            nanoContainerXml = "config/nanocontainer.xml";
        }
        NanoContainer nano = new NanoContainer(new FileReader(nanoContainerXml));
        addShutdownHook(nano);
    }

    protected static void addShutdownHook(final NanoContainer nano) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                nano.stopComponentsDepthFirst();
                nano.disposeComponentsDepthFirst();
            }
        });
    }
}
