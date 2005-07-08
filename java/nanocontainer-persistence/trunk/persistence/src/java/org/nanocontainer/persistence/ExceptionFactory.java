package org.nanocontainer.persistence;

/**
 * This component creates your own exceptions. It'll be used for ExceptionHandler in order to create exceptions.
 * 
 * @see org.nanocontainer.persistence.ExceptionFactory
 * @version $Id$
 */
public interface ExceptionFactory {

	/**
	 * Creates an instance of the DAO base exception. You should return <code>cause</code> if it is already an
	 * instance of DAO's exception.
	 * 
	 * @param cause Original exception.
	 * @return The desired exception instance.
	 */
	public RuntimeException createDAOException(Throwable cause);

	/**
	 * Creates an instance of the exception which indicates concurrency failure.
	 * 
	 * @param cause Original exception.
	 * @return The desired exception instance.
	 */
	public RuntimeException createConcurrencyFailureException(Throwable cause);

	/**
	 * Creates an instance of the exception which indicates that the version number or timestamp check failed or try
	 * delete or update a row that does not exist anymore. It should be subclass of the one which indicates concurrency
	 * failure.
	 * 
	 * @param cause Original exception.
	 * @param type A string which indicate which entity it has happened or null if it can't be determined.
	 * @param id The id representation of its object or null if it can't be determined.
	 * @return The desired exception instance.
	 */
	public RuntimeException createStaleObjectStateException(Throwable cause, String type, Object id);

	/**
	 * Creates an instance of the exception which indicates that an object retrieval failure happens.
	 * 
	 * @param cause Original exception.
	 * @param type A string which indicate which entity it has happened or null if it can't be determined.
	 * @param id The id representation of its object or null if it can't be determined.
	 * @return The desired exception instance.
	 */
	public RuntimeException createObjectRetrievalFailureException(Throwable cause, String type, Object id);

	/**
	 * Creates an instance of the exception which indicates that a transaction could not be begun, committed or rolled
	 * back.
	 * 
	 * @param cause Original exception.
	 * @return The desired exception instance.
	 */
	public RuntimeException createTransactionException(Throwable cause);

}
