package org.nanocontainer.persistence.e;

/**
 * Thrown when, guess what, an object retrieval failure happens.
 * 
 * @version $Id$
 */
public class ObjectRetrievalFailureException extends PicoDAOException implements EntityInfo {

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
