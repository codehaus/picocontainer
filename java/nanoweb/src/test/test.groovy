package org.nanocontainer.nanoweb;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MyGroovyAction {

    property year
    property cars

    public String execute() {
        if(year > 2003) {
            return "success"
        } else {
            return "error"
        }
    }
}