/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.tools;

import org.picocontainer.Startable;
import net.sf.hibernate.cfg.Configuration;
import java.util.Properties;
import net.sf.hibernate.HibernateException;

/**
 * startatble schema creater and dropper.
 *
 * @author    kostik
 * @created   November 24, 2004
 * @version   $Revision$
 */
public class SchemaExport extends net.sf.hibernate.tool.hbm2ddl.SchemaExport implements Startable {

    /**
     * Constructor for the SchemaExport object
     *
     * @param configuration           Description of Parameter
     * @exception HibernateException  Description of Exception
     */
    public SchemaExport(Configuration configuration) throws HibernateException {
        super(configuration);
    }


    /**
     * Constructor for the SchemaExport object
     *
     * @param configuration           Description of Parameter
     * @param properties              Description of Parameter
     * @exception HibernateException  Description of Exception
     */
    public SchemaExport(Configuration configuration, Properties properties) throws HibernateException {
        super(configuration, properties);
    }


    /**
     * DOCUMENT METHOD
     */
    public void start() {
        try {
            System.err.println("********************** exporting schema***************");
            drop(false, true);
            create(false, true);
            System.err.println("**********************done ***************");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            throw new RuntimeException(ex);
        }
    }


    /**
     * DOCUMENT METHOD
     */
    public void stop() {
        try {
            drop(false, true);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
