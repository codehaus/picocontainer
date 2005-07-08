package org.nanocontainer.persistence.e;

/**
 * Indicates that a transaction could not be begun, committed or rolled back.
 * 
 * @version $Id$
 */
public class TransactionException extends PicoDAOException {

	public TransactionException(Throwable cause) {
		super(cause);
	}

}
