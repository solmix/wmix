package org.solmix.rest.route;


/**
 * Created by ice on 15-1-26.
 */
public abstract class Validator {

    public abstract ValidResult validate(Params params, RouteMatch routeMatch);

}
