package org.nanocontainer.nanoweb;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MyGroovyAction {

    property year
    property cars
    property country

    public execute() {
        // success if there is a renault among the cars
        for( car in cars ) {
            if( car == "renault" ) {
                return "success"
            }
        }
        return "error"
    }
}