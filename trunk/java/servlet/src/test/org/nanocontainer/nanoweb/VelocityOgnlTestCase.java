package org.nanocontainer.nanoweb;

import junit.framework.TestCase;
import ognl.Ognl;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class VelocityOgnlTestCase extends TestCase {

    public void testCollectionsCanBeNavigated() throws Exception {
        VelocityEngine velocityEngine = new VelocityEngine();
        Properties properties = new Properties();
        properties.setProperty( "resource.loader", "classpath");
        properties.setProperty( "classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init(properties);

        VelocityContext context = new VelocityContext();
        MyAction action = new MyAction();

        Collection cars = new ArrayList();
        for(int i = 0; i < 2; i++ ) {
            cars.add(new Car());
        }
        Ognl.setValue("cars", action, cars);

        Ognl.setValue("cars[0].name", action, "mercedes");
        Ognl.setValue("cars[1].name", action, "volkswagen");

        context.put("action", action);

        StringWriter output = new StringWriter();
        velocityEngine.mergeTemplate("test.vm",
                context,
                output);
        String outputString = output.getBuffer().toString();
        assertTrue(outputString.indexOf("mercedes") > -1);
        assertTrue(outputString.indexOf("volkswagen") > -1);
    }

}