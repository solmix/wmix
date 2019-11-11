
package org.solmix.rest.route.interceptor;

import org.solmix.rest.RestException;

/**
 * InterceptorException
 */
public class InterceptorException extends RestException {

    public InterceptorException() {
    }

    public InterceptorException(String message) {
        super(message);
    }

    public InterceptorException(Throwable cause) {
        super(cause);
    }

    public InterceptorException(String message, Throwable cause) {
        super(message, cause);
    }
}










