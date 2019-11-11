package org.solmix.rest.route;

import static org.solmix.commons.util.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.rest.exception.WebException;
import org.solmix.rest.http.HttpRequest;
import org.solmix.rest.http.HttpResponse;
import org.solmix.rest.http.HttpStatus;
import org.solmix.rest.route.interceptor.Interceptor;

/**
 * ActionInvocation invoke the action
 */
public class RouteInvocation {

    private final static Logger logger = LoggerFactory.getLogger(RouteInvocation.class);
    private Route route;
    private RouteMatch routeMatch;
    private Interceptor[] interceptors;
    private int index = 0;
    private boolean wasInvoke = false;
    private Object invokeResult = null;

    // ActionInvocationWrapper need this constructor
    private RouteInvocation() {

    }

    public RouteInvocation(Route route, RouteMatch routeMatch) {
        this.route = route;
        this.routeMatch = routeMatch;
        this.interceptors = route.getInterceptors();
    }

    /**
     * Invoke the route.
     */
    private void methodInvoke() {
        if (index < interceptors.length) {
            interceptors[index++].intercept(this);
        } else if (index++ == interceptors.length) {
            Object resource;
            try {
                    resource = route.getResourceClass().newInstance();

                assertNotNull(resource, "Could init '" + route.getResourceClass() + "' before invoke method.");
                //获取所有参数
                Params params = routeMatch.getParams();

                //数据验证
                validate(params);
                Method method = route.getMethod();
                method.setAccessible(true);
                //执行方法
                if (route.getAllParamNames().size() > 0) {
                    List<Class<?>> allParamTypes = route.getAllParamTypes();
                    List<String> allParamNames = route.getAllParamNames();
                    //执行方法的参数
                    Object[] args = new Object[allParamNames.size()];
                    int i = 0;
                    for (String name : allParamNames) {
                        if (HttpRequest.class.isAssignableFrom(allParamTypes.get(i))) {
                            args[i++] = routeMatch.getRequest();
                        } else if (HttpResponse.class.isAssignableFrom(allParamTypes.get(i))) {
                            args[i++] = routeMatch.getResponse();
                        } else if (Headers.class.isAssignableFrom(allParamTypes.get(i))) {
                            args[i++] = routeMatch.getHeaders();
                        } else if (Params.class.isAssignableFrom(allParamTypes.get(i))) {
                            args[i++] = routeMatch.getParams();
                        } else {
                            args[i++] = params.get(name);
                        }
                    }
                    invokeResult = method.invoke(resource, args);
                } else {
                    invokeResult = method.invoke(resource);
                }
                wasInvoke = true;
            } catch (Exception e) {
                route.throwException(e);
            }
        }
    }


    

    /**
     * 请求参数验证
     *
     * @param params 参数
     */
    private void validate(Params params) {
        Validator[] validators = route.getValidators();

        if (validators.length > 0) {
            Map<String, Object> errors = new HashMap<String, Object>();
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            ValidResult vr;

            for (Validator validator : validators) {
                //数据验证
                vr = validator.validate(params, routeMatch);
                errors.putAll(vr.getErrors());
                if (!status.equals(vr.getStatus()))
                    status = vr.getStatus();

                if (errors.size() > 0) {
                    throw new WebException(status, errors);
                }
            }


        }
    }


    public Method getMethod() {
        return route.getMethod();
    }

    public Class getResourceClass() {
        return route.getResourceClass();
    }

    public RouteMatch getRouteMatch() {
        return routeMatch;
    }

    public Object invoke() {
        if (!wasInvoke) {
            methodInvoke();
        }
        return invokeResult;
    }
}
