package org.synchrotron.core;
public class SyncRunnerException extends RuntimeException {

	public SyncRunnerException(String message) {
		super(message);
	}

	public SyncRunnerException(String message, Throwable cause) {
		super(message, cause);
	}
}
