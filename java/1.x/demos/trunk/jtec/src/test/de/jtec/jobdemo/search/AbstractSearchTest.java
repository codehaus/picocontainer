/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.search;
import junit.framework.TestCase;

import org.apache.velocity.app.VelocityEngine;
import org.picocontainer.gems.util.ConstructableProperties;

/**
 * abstract base class for search system testing
 *
 * @author    kostik
 * @created   December 1, 2004
 * @version   $Revision$
 */
public abstract class AbstractSearchTest extends TestCase {

    DocumentFactory _factory;


    /**
     * Gets the Factory attribute of the AbstractSearchTest object
     *
     * @return   The Factory value
     */
    public DocumentFactory getFactory() {
        return _factory;
    }


    /**
     * set up directory for indexing
     *
     * @exception Exception  Description of Exception
     */
    public void setUp() throws Exception {
        VelocityEngine engine = new VelocityEngine(new ConstructableProperties("velocity.properties"));
        _factory = new DocumentFactory(engine);
    }
}

