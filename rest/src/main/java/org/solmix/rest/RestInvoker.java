
package org.solmix.rest;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.Message;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.invoker.Invoker;
import org.solmix.rest.exception.WebException;
import org.solmix.rest.http.HttpMethod;
import org.solmix.rest.http.HttpRequest;
import org.solmix.rest.http.HttpResponse;
import org.solmix.rest.http.HttpStatus;
import org.solmix.rest.route.Route;
import org.solmix.rest.route.RouteInvocation;
import org.solmix.rest.route.RouteMatch;
import org.solmix.rest.route.RouteRepository;
import org.solmix.wmix.exchange.WmixMessage;

public class RestInvoker implements Invoker {
	private static final Logger LOG = LoggerFactory.getLogger(RestInvoker.class);

	@Override
	public Object invoke(Exchange exchange, Object o) {
		HttpServletRequest servletRequest = (HttpServletRequest) exchange.get(WmixMessage.REQUEST);
		HttpServletResponse servletResponse = (HttpServletResponse) exchange.get(WmixMessage.RESPONSE);
		String encoding  = (String)exchange.get(Message.ENCODING);
		try {
			servletRequest.setCharacterEncoding(encoding);
			servletResponse.setCharacterEncoding(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new Fault(e);
		}
		RouteRepository repository = exchange.get(RouteRepository.class);
		HttpRequest request = new HttpRequest(servletRequest);
		HttpResponse response = new HttpResponse(servletResponse, servletRequest);
		Object result = handle(request, response, repository);
		return result;
	}

	private Object handle(HttpRequest request, HttpResponse response, RouteRepository repository) {
		RouteMatch routeMatch = null;
		Route route = null;
		RouteInvocation routeInvocation = null;
		// 请求的rest路径
		String restPath = request.getRestPath();
		Map<String, Map<String, Set<Route>>> routesMap = repository.getRoutesMap();
		Set<Route> routesSet;
		String httpMethod = request.getHttpMethod();
		boolean supportMethod = HttpMethod.support(httpMethod);
		// httpmethod区分
		if (supportMethod) {
			if (routesMap.containsKey(httpMethod)) {
				Set<Map.Entry<String, Set<Route>>> routesEntrySet = routesMap.get(httpMethod).entrySet();
				// url区分
				for (Map.Entry<String, Set<Route>> routesEntry : routesEntrySet) {
					if (restPath.startsWith(routesEntry.getKey())) {
						routesSet = routesEntry.getValue();
						for (Route r : routesSet) {
							routeMatch = r.match(request, response);
							if (routeMatch != null) {
								route = r;
								break;
							}
						}
						if (routeMatch != null) {
							break;
						}
					}
				}
			}

			if (routeMatch != null) {
				routeInvocation = new RouteInvocation(route, routeMatch);
			}
		}
		if (routeInvocation != null) {
			return routeInvocation.invoke();
		} else {
			if (!restPath.equals("/") && supportMethod) {
				// no route matched
				throw new WebException(HttpStatus.SERVICE_UNAVAILABLE, "API is unavailable,check request body.");
			}
			return null;
		}
	}

}
