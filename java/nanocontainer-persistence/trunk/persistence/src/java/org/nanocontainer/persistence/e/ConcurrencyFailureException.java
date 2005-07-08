package org.nanocontainer.persistence.e;

/**
 * Thrown when optimistic locking or failure to acquire lock happens.
 * 
 * @see org.nanocontainer.persistence.e.StaleObjectStateException
 * 
 * @version $Id$
 */
public class ConcurrencyFailureException extends PicoDAOException {

    public ConcurrencyFailureException(Throwable cause) {
    	super(cause);
    }
    
}
