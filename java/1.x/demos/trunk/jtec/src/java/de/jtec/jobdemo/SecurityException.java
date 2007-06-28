/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo;

/**
 * exception to be used on security problems
 *
 * @author   Konstantin Pribluda
 * @created   November 20, 2004
 * @version   $Revision$
 */
public class SecurityException extends JobdemoException {
    /**
     * default serial version UID
     */
    private static final long serialVersionUID = 1L;


    /**
     * Creates a new SecurityException object.
     *
     * @param message  message
     */
    public SecurityException(String message) {
        super(message);
    }


    /**
     * Creates a new SecurityException object.
     *
     * @param cause  original cause
     */
    public SecurityException(Throwable cause) {
        super(cause);
    }
}
