package org.solmix.rest.route.interceptor;


import org.solmix.rest.route.RouteInvocation;

/**
 * Interceptor.
 */
public interface Interceptor {
    public void intercept(RouteInvocation ri);
}
