package org.solmix.rest.route;

import org.solmix.rest.RestException;

public class InitException extends RestException {
	  public InitException() {
	    }

	    public InitException(String message) {
	        super(message);
	    }

	    public InitException(Throwable cause) {
	        super(cause);
	    }

	    public InitException(String message, Throwable cause) {
	        super(message, cause);
	    }
}
