package org.nanocontainer.persistence;

import org.nanocontainer.persistence.e.ConcurrencyFailureException;
import org.nanocontainer.persistence.e.ObjectRetrievalFailureException;
import org.nanocontainer.persistence.e.PicoDAOException;
import org.nanocontainer.persistence.e.StaleObjectStateException;
import org.nanocontainer.persistence.e.TransactionException;

/**
 * It is a factory for the nanodao defaults exceptions (package org.nanocontainer.persistence.e).
 * 
 * @see org.nanocontainer.persistence.e
 * @version $Id$
 */
public class DefaultExceptionFactory implements ExceptionFactory {

	/**
	 * @return org.nanocontainer.persistence.e.UnknowException
	 */
	public RuntimeException createDAOException(Throwable cause) {
		if (cause instanceof PicoDAOException) {
			return (PicoDAOException) cause;
		}

		return new PicoDAOException(cause);
	}

	/**
	 * @return org.nanocontainer.persistence.e.ConcurrencyFailureException
	 */
	public RuntimeException createConcurrencyFailureException(Throwable cause) {
		return new ConcurrencyFailureException(cause);
	}

	/**
	 * @return org.nanocontainer.persistence.e.ObjectRetrievalFailureException
	 */
	public RuntimeException createStaleObjectStateException(Throwable cause, String type, Object id) {
		return new StaleObjectStateException(cause, type, id);
	}

	/**
	 * @return org.nanocontainer.persistence.e.ObjectRetrievalFailureException
	 */
	public RuntimeException createObjectRetrievalFailureException(Throwable cause, String type, Object id) {
		return new ObjectRetrievalFailureException(cause, type, id);
	}

	/**
	 * @return org.nanocontainer.persistence.e.TransactionException
	 */
	public RuntimeException createTransactionException(Throwable cause) {
		return new TransactionException(cause);
	}
}
