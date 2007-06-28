package org.nanocontainer.persistence;

/**
 * Thrown when an object retrieval failure occurs.
 * 
 * @version $Revision: $
 */
public class ObjectRetrievalFailureException extends PersistenceException implements EntityInfo {

	private String entityName;
	private Object objectId;

	public ObjectRetrievalFailureException(Throwable cause, String entityName, Object objectId) {
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
