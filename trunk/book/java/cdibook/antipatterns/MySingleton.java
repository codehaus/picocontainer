package cdibook.antipatterns;

/**
 * @author Mike Mason. Master of crap code.
 * @version $Revision$
 */

// START SNIPPET: class

public class MySingleton {

    private static final MySingleton instance = new MySingleton();

    private MySingleton() {
    }

    public MySingleton getInstance() {
        return instance;
    }

// END SNIPPET: class

}

