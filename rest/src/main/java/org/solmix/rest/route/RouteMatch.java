package org.solmix.rest.route;

import static org.solmix.commons.util.Assert.assertNotNull;

import org.solmix.rest.http.HttpRequest;
import org.solmix.rest.http.HttpResponse;

/**
 * Created by ice on 14-12-19.
 */
public class RouteMatch {

    private final String pattern;
    private final String path;
    private final String extension;
    private final Params params;
    private final Headers headers;
    private final HttpRequest request;
    private final HttpResponse response;

    public RouteMatch(String pattern, String path, String extension,
                      Params params, HttpRequest request, HttpResponse response) {

        this.pattern = assertNotNull(pattern);
        this.path = assertNotNull(path);
        this.params = assertNotNull(params);
        this.extension = assertNotNull(extension);
        this.request = assertNotNull(request);
        this.headers = new Headers(request.getHeaders());
        this.response = assertNotNull(response);
    }

    public String getPath() {
        return path;
    }

    public Params getParams() {
        return params;
    }

    public String getExtension() {
        return extension;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String toString() {
        return "RouteMatch{" +
                "pattern='" + pattern + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
