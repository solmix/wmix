package org.solmix.wmix.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Files;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.runtime.resource.support.PathMatchingResourceResolver;
import org.solmix.runtime.resource.support.URLResource;

public class ServletContextResourceResolver extends PathMatchingResourceResolver {
    private static final Logger LOG = LoggerFactory.getLogger(ServletContextResourceResolver.class);
	  ServletContext servletContext;
	    Map<String, URL> urlMap = new ConcurrentHashMap<String, URL>();

	    public ServletContextResourceResolver(ServletContext sc) {
	        servletContext = sc;
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
	    @Override
	      protected Set<InputStreamResource> doFindPathMatchingFileResources(InputStreamResource rootDirResource, String subPattern)
	                  throws IOException {

	            if (rootDirResource instanceof ServletContextResource) {
	                  ServletContextResource scResource = (ServletContextResource) rootDirResource;
	                  ServletContext sc = scResource.getServletContext();
	                  String fullPattern = scResource.getPath() + subPattern;
	                  Set<InputStreamResource> result = new LinkedHashSet<InputStreamResource>(8);
	                  doRetrieveMatchingServletContextResources(sc, fullPattern, scResource.getPath(), result);
	                  return result;
	            }
	            else {
	                  return super.doFindPathMatchingFileResources(rootDirResource, subPattern);
	            }
	      }

	      /**
	       * Recursively retrieve ServletContextResources that match the given pattern,
	       * adding them to the given result set.
	       * @param servletContext the ServletContext to work on
	       * @param fullPattern the pattern to match against,
	       * with preprended root directory path
	       * @param dir the current directory
	       * @param result the Set of matching Resources to add to
	       * @throws IOException if directory contents could not be retrieved
	       * @see ServletContextResource
	       * @see javax.servlet.ServletContext#getResourcePaths
	       */
	      protected void doRetrieveMatchingServletContextResources(
	                  ServletContext servletContext, String fullPattern, String dir, Set<InputStreamResource> result)
	                  throws IOException {

	            Set<String> candidates = servletContext.getResourcePaths(dir);
	            if (candidates != null) {
	                  boolean dirDepthNotFixed = fullPattern.contains("**");
	                  int jarFileSep = fullPattern.indexOf(Files.JAR_URL_SEPARATOR);
	                  String jarFilePath = null;
	                  String pathInJarFile = null;
	                  if (jarFileSep > 0 && jarFileSep + Files.JAR_URL_SEPARATOR.length() < fullPattern.length()) {
	                        jarFilePath = fullPattern.substring(0, jarFileSep);
	                        pathInJarFile = fullPattern.substring(jarFileSep + Files.JAR_URL_SEPARATOR.length());
	                  }
	                  for (String currPath : candidates) {
	                        if (!currPath.startsWith(dir)) {
	                              // Returned resource path does not start with relative directory:
	                              // assuming absolute path returned -> strip absolute path.
	                              int dirIndex = currPath.indexOf(dir);
	                              if (dirIndex != -1) {
	                                    currPath = currPath.substring(dirIndex);
	                              }
	                        }
	                        if (currPath.endsWith("/") && (dirDepthNotFixed || StringUtils.countOccurrencesOf(currPath, "/") <=
	                                    StringUtils.countOccurrencesOf(fullPattern, "/"))) {
	                              // Search subdirectories recursively: ServletContext.getResourcePaths
	                              // only returns entries for one directory level.
	                              doRetrieveMatchingServletContextResources(servletContext, fullPattern, currPath, result);
	                        }
	                        if (jarFilePath != null && matcher.match(jarFilePath, currPath)) {
	                              // Base pattern matches a jar file - search for matching entries within.
	                              String absoluteJarPath = servletContext.getRealPath(currPath);
	                              if (absoluteJarPath != null) {
	                                    doRetrieveMatchingJarEntries(absoluteJarPath, pathInJarFile, result);
	                              }
	                        }
	                        if (matcher.match(fullPattern, currPath)) {
	                              result.add(new ServletContextResource(servletContext, currPath));
	                        }
	                  }
	            }
	      }

	      /**
	       * Extract entries from the given jar by pattern.
	       * @param jarFilePath the path to the jar file
	       * @param entryPattern the pattern for jar entries to match
	       * @param result the Set of matching Resources to add to
	       */
	      private void doRetrieveMatchingJarEntries(String jarFilePath, String entryPattern, Set<InputStreamResource> result) {
	            if (LOG.isDebugEnabled()) {
	                  LOG.debug("Searching jar file [" + jarFilePath + "] for entries matching [" + entryPattern + "]");
	            }
	            try {
	                  JarFile jarFile = new JarFile(jarFilePath);
	                  try {
	                        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
	                              JarEntry entry = entries.nextElement();
	                              String entryPath = entry.getName();
	                              if (matcher.match(entryPattern, entryPath)) {
	                                    result.add(new URLResource(
	                                                Files.URL_PROTOCOL_JAR,
	                                                Files.FILE_URL_PREFIX + jarFilePath + Files.JAR_URL_SEPARATOR + entryPath));
	                              }
	                        }
	                  }
	                  finally {
	                        jarFile.close();
	                  }
	            }
	            catch (IOException ex) {
	                  if (LOG.isWarnEnabled()) {
	                        LOG.warn("Cannot search for matching resources in jar file [" + jarFilePath +
	                                    "] because the jar cannot be opened through the file system", ex);
	                  }
	            }
	      }
}
