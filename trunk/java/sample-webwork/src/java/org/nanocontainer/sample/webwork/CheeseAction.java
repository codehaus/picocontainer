/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.sample.webwork;

import org.nanocontainer.sample.webwork.dao.CheeseDao;
import webwork.action.ActionSupport;

public class CheeseAction extends ActionSupport {
    private final CheeseDao dao;
    private Cheese cheese;

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
            return ERROR;
        }
    }
}
