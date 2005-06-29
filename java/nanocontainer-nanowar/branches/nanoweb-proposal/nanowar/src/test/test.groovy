package org.nanocontainer.dynaweb;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1570 $
 */
public class MyGroovyAction {

    property year

    public doit() {
        if(year > 2003) {
            return "success";
        } else {
            return "error";
        }
    }
}