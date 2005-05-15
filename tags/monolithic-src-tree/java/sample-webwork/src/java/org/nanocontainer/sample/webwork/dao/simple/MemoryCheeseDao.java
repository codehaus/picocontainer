/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.sample.webwork.dao.simple;

import org.nanocontainer.sample.webwork.Cheese;
import org.nanocontainer.sample.webwork.dao.CheeseDao;
import org.picocontainer.Startable;

import java.util.HashMap;
import java.util.Map;

public class MemoryCheeseDao implements CheeseDao, Startable {
    private Map cheeses = new HashMap();

    public void saveCheese(Cheese cheese) {
        System.out.println("**** MemoryCheeseDao saving cheese: " + cheese.getName());
        cheeses.put(cheese.getName(), cheese);
    }

    public Cheese findCheese(Cheese example) {
        System.out.println("**** MemoryCheeseDao finding cheese: " + example);
        return (Cheese) cheeses.get(example.getName());
    }

    public void start() {
        System.out.println("**** MemoryCheeseDao started");
    }

    public void stop() {
        System.out.println("**** MemoryCheeseDao stopped");
    }
}