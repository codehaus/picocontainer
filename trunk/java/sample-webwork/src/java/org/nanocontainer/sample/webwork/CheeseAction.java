/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.sample.webwork;

import org.nanocontainer.sample.webwork.dao.CheeseDao;
import webwork.action.ActionSupport;
import webwork.action.CommandDriven;

/**
 * Example of a WebWork action that relies on constructor injection.
 */
public class CheeseAction extends ActionSupport implements CommandDriven {
    private final CheeseDao dao;

    private Cheese cheese = new Cheese();

    public CheeseAction(CheeseDao dao) {
        this.dao = dao;
    }

    public Cheese getCheese() {
        return cheese;
    }

    public String doSave() {
        try {
            dao.saveCheese(cheese);
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage("Couldn't save cheese: " + cheese);
            return ERROR;
        }
    }

    public String doFind() {
        try {
            dao.findCheese(cheese);
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage("Couldn't save cheese: " + cheese);
            return ERROR;
        }
    }
}
