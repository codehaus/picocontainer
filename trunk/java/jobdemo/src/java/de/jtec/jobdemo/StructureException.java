/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo;

/**
 * exception to be thrown on internal problems
 *
 * @author    Konstantin Pribluda
 * @created   November 20, 2004
 * @version   $Revision$
 */
public class StructureException extends JobdemoException {
    /**
     * default serial version uid
     */
    private static final long serialVersionUID = 1L;


    /**
     * Creates a new StructureException object.
     *
     * @param message  exception message
     */
    public StructureException(String message) {
        super(message);
    }


    /**
     * Creates a new StructureException object.
     *
     * @param cause  original cause
     */
    public StructureException(Throwable cause) {
        super(cause);
    }
}
