/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo;

/**
 * generic exception 
 *
 * @author    Konstantin Pribluda
 * @created   November 20, 2004
 * @version   $Revision$
 */
public class JobdemoException extends Exception {
    /**
     * default serial version UID
     */
    private static final long serialVersionUID = 1L;


    /**
     * Creates a new JobdemoException object.
     *
     * @param message  DOCUMENT ME!
     */
    public JobdemoException(String message) {
        super(message);
    }


    /**
     * Creates a new JobdemoException object.
     *
     * @param cause  DOCUMENT ME!
     */
    public JobdemoException(Throwable cause) {
        super(cause);
    }
}
