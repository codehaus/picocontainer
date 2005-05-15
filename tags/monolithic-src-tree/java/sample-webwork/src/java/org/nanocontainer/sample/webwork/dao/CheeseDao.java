/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 31-Jan-2004
 * Time: 00:55:03
 */
package org.nanocontainer.sample.webwork.dao;

import org.nanocontainer.sample.webwork.Cheese;

public interface CheeseDao {
    void saveCheese(Cheese cheese);

    Cheese findCheese(Cheese example);
}