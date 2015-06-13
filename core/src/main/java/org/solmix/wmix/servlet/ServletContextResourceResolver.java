package org.solmix.wmix.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;

import org.solmix.runtime.resource.ResourceResolverAdaptor;

public class ServletContextResourceResolver extends ResourceResolverAdaptor {
	  ServletContext servletContext;
	    Map<String, URL> urlMap = new ConcurrentHashMap<String, URL>();

	    public ServletContextResourceResolver(ServletContext sc) {
	        servletContext = sc;
	    }


	    public final InputStream getAsStream(final String string) {
	        if (urlMap.containsKey(string)) {
	            try {
	                return urlMap.get(string).openStream();
	            } catch (IOException e) {
	                //ignore
	            }
	        }
	        return servletContext.getResourceAsStream(string);
	    }

	    public final <T> T resolve(final String entryName, final Class<T> clz) {

	        Object obj = null;
	        try {
	            if (entryName != null) {
	                InitialContext ic = new InitialContext();
	                try {
	                    obj = ic.lookup(entryName);
	                } finally {
	                    ic.close();
	                }
	            }
	        } catch (Throwable e) {
	            //do nothing
	        }

	        if (obj != null && clz.isInstance(obj)) {
	            return clz.cast(obj);
	        }

	        if (clz.isAssignableFrom(URL.class)) {
	            if (urlMap.containsKey(entryName)) {
	                return clz.cast(urlMap.get(entryName));
	            }
	            try {
	                URL url = servletContext.getResource(entryName);
	                if (url != null
	                    && "file".equals(url.getProtocol())
	                    && !(new File(url.toURI()).exists())) {
	                    url = null;
	                }
	                if (url != null) {
	                    urlMap.put(url.toString(), url);
	                    return clz.cast(url);
	                }
	            } catch (MalformedURLException e) {
	                //fallthrough
	            } catch (URISyntaxException e) {
	                //ignore
	            }
	            try {
	                URL url = servletContext.getResource("/" + entryName);
	                if (url != null
	                    && "file".equals(url.getProtocol())
	                    && !(new File(url.toURI()).exists())) {
	                    url = null;
	                }
	                if (url != null) {
	                    urlMap.put(url.toString(), url);
	                    return clz.cast(url);
	                }
	            } catch (MalformedURLException e1) {
	                //ignore
	            } catch (URISyntaxException e) {
	                //ignore
	            }
	        } else if (clz.isAssignableFrom(InputStream.class)) {
	            return clz.cast(getAsStream(entryName));
	        }
	        return null;
	    }
}
