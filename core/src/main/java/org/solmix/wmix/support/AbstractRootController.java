package org.solmix.wmix.support;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ServletUtils;
import org.solmix.wmix.Components;
import org.solmix.wmix.RootController;
import org.solmix.wmix.WmixConfiguration;
import org.solmix.wmix.servlet.PassThroughSupport;
import org.solmix.wmix.util.RequestURIFilter;

public abstract class AbstractRootController implements RootController, PassThroughSupport {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private Components components;
	
    private RequestURIFilter passThroughFilter;
	@Override
	public void setPassThroughFilter(RequestURIFilter passthru) {
		this.passThroughFilter=passthru;
	}

	@Override
	public void init(Components components) {
		this.components = components;
		WmixConfiguration config = getWmixConfiguration();
		log.debug("Initailizing Wmix Root Controller in {} mode,according to <configuration>",
				config.isProductionMode() ? "production" : "developmment");

	}

	public WmixConfiguration getWmixConfiguration() {
		return getComponents().getParentWmixConfiguration();
	}

	public Components getComponents() {
		return components;
	}

	@Override
	public void service(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain) throws Exception {
		  if (isRequestPassedThrough(request) || !invoke(request,response)) {
              // 交给容器处理
              giveUpControl(request,response, chain);
          }
	}

	private void giveUpControl(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		 try {
			 response.setContentType(null);
	        } catch (Exception e) {
	            // ignored
	        }
		 chain.doFilter(request, response);
	}

	protected abstract boolean invoke(HttpServletRequest request,	HttpServletResponse response)  throws Exception ;

	private boolean isRequestPassedThrough(HttpServletRequest request) {
		 if (passThroughFilter != null) {
	            String path = ServletUtils.getResourcePath(request);
	            if (passThroughFilter.matches(path)) {
	                log.debug("Passed through request: {}", path);
	                return true;
	            }
	        }
	        return false;
	}

}
