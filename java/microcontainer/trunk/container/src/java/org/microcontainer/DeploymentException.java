package org.microcontainer;

public class DeploymentException extends Exception {

	public DeploymentException() {
		super();
	}

	public DeploymentException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeploymentException(Throwable cause) {
		super(cause);
	}

	public DeploymentException(String message) {
		super(message);
	}
}
