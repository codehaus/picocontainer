package org.nanocontainer.nanowar.struts;

import junit.framework.TestCase;
import org.nanocontainer.sample.nanowar.dao.CheeseDao;
import org.nanocontainer.sample.nanowar.service.CheeseService;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.script.xml.XStreamContainerBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

import java.io.Reader;
import java.io.StringReader;

/**
 * @author Mauro Talevi
 */
public class ActionsContainerTestCase extends TestCase {

    private ObjectReference containerRef = new SimpleReference();

    private ObjectReference parentContainerRef = new SimpleReference();

    protected PicoContainer buildContainer(Reader script) {
        ScriptedContainerBuilder builder = new XStreamContainerBuilder(script,
                getClass().getClassLoader());
        parentContainerRef.set(null);
        builder.buildContainer(containerRef, parentContainerRef, "SOME_SCOPE", true);
        return (PicoContainer) containerRef.get();
    }

    public void testContainerBuildingWithXmlConfig() {

        Reader script = new StringReader("<container>"
                + "	 <implementation type='org.nanocontainer.sample.nanowar.dao.CheeseDao'"
                + "					class='org.nanocontainer.sample.nanowar.dao.simple.MemoryCheeseDao'> "
                + "  </implementation>"
                + "	 <implementation type='org.nanocontainer.sample.nanowar.service.CheeseService'"
                + " 				class='org.nanocontainer.sample.nanowar.service.defaults.DefaultCheeseService'>"
                + "  </implementation>"
                + " </container>");

        PicoContainer pico = buildContainer(script);
        assertNotNull(pico.getComponentInstanceOfType(CheeseDao.class));
        assertNotNull(pico.getComponentInstanceOfType(CheeseService.class));
    }

}