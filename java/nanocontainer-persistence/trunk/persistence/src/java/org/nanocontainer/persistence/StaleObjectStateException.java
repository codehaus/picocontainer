package org.nanocontainer.persistence;

/**
 * A specialization of {@link org.nanocontainer.persistence.ConcurrencyFailureException ConcurrencyFailureException}
 * which is thrown when a version number or timestamp check failed or try delete or update a row that does not exist
 * anymore.
 * 
 * @version $Revision: $
 */
public class StaleObjectStateException extends ConcurrencyFailureException {

	private String entityName;
	private Object objectId;

	public StaleObjectStateException(Throwable cause, String entityName, Object objectId) {
		super(cause);
		this.entityName = entityName;
		this.objectId = objectId;
	}

	public String getEntityName() {
		return entityName;
	}

	public Object getObjectId() {
		return objectId;
	}

}
