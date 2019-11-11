package org.solmix.rest.route;

import java.util.Map;
import java.util.Set;

public interface RouteRepository {

	void init();

	void setIncludePackages(Set<String> includePackages);

	void setExcludePackages(Set<String> excludePackages);

	Map<String, Map<String, Set<Route>>> getRoutesMap();

	void setComponentPath(String path);

}
