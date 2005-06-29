package org.nanocontainer.nanowar.nanoweb.example.bookmarker;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.Disposable;

public class HSQLDBDatabaseComponent implements Disposable {

    private transient Log log = LogFactory.getLog(HSQLDBDatabaseComponent.class);

    public transient Connection conn;

    public HSQLDBDatabaseComponent() {
        try {
            long inicio = System.currentTimeMillis();
            Class.forName("org.hsqldb.jdbcDriver");
            this.conn = DriverManager.getConnection("jdbc:hsqldb:mem:test");
            this.conn.createStatement().execute(IOUtils.toString(HSQLDBDatabaseComponent.class.getResourceAsStream("database.sql")));

            log.info("Test Database created in " + (System.currentTimeMillis() - inicio) + "ms.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void dispose() {
        try {
            this.conn.createStatement().execute("SHUTDOWN");
            log.info("Test Database ended.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
