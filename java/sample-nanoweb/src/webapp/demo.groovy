package org.nanocontainer.sample.nanoweb;

/**
 * Simple Nanoweb action. It's a POJO!
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DemoAction {

    property magic = "This is not correct";

    public execute() {
        if (magic.equals("NanoWeb")) {
            return "input";
        } else {
            return "success";
        }
    }
}