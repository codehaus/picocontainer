package org.nanocontainer.type3msg;

import org.picocontainer.PicoException;

/**
 * Created by IntelliJ IDEA.
 * User: skizz
 * Date: Sep 10, 2003
 * Time: 8:58:44 PM
 * To change this template use Options | File Templates.
 */
public class MulticastingPicoException extends PicoException {
    public MulticastingPicoException(String string) {
        super(string);
    }
}
