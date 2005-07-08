package org.nanocontainer.persistence.hibernate;

public interface HibernateExceptionHandler {

	public RuntimeException handle(Throwable ex);

}
