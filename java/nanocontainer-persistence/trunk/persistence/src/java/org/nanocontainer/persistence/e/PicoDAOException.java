package org.nanocontainer.persistence.e;

import org.picocontainer.PicoException;

/**
 * Base for all DAO related exceptions.
 * 
 * @version $Id$
 */
public class PicoDAOException extends PicoException {

	public PicoDAOException(Throwable cause) {
		super(cause);
	}

}
