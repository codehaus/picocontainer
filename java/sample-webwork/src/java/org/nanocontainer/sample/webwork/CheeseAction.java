/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.sample.webwork;

import org.nanocontainer.sample.webwork.dao.CheeseDao;
import webwork.action.ActionSupport;

/**
 * Example of a WebWork action that relies on constructor injection.
 */
public class CheeseAction extends ActionSupport {
    private final CheeseDao dao;
    private Cheese cheese = new Cheese();

    public CheeseAction(CheeseDao dao) {
        this.dao = dao;
    }

    public Cheese getCheese() {
        return cheese;
    }

    public void setCheese(Cheese cheese) {
        this.cheese = cheese;
    }

    public String execute() {
        try {
            dao.saveCheese(cheese);
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace(

            );
            return ERROR;
        }
    }
}
