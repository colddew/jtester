package org.jtester.exception;

/**
 * 禁止api调用的异常
 * 
 * @author darui.wudr
 * 
 */
public class ForbidCallException extends RuntimeException {

	private static final long serialVersionUID = 1966484423238025964L;

	public ForbidCallException() {
		super();
	}

	public ForbidCallException(String message, Throwable cause) {
		super(message, cause);
	}

	public ForbidCallException(String message) {
		super(message);
	}

	public ForbidCallException(Throwable cause) {
		super(cause);
	}
}
