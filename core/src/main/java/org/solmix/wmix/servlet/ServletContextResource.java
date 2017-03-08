/*
 * Copyright 2014 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */
package org.solmix.wmix.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.FileUtils;
import org.solmix.runtime.resource.support.AbstractFileStreamResource;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月23日
 */

public class ServletContextResource extends AbstractFileStreamResource
{


    private final ServletContext servletContext;

    private final String path;


    /**
     * Create a new ServletContextResource.
     * <p>The Servlet spec requires that resource paths start with a slash,
     * even if many containers accept paths without leading slash too.
     * Consequently, the given path will be prepended with a slash if it
     * doesn't already start with one.
     * @param servletContext the ServletContext to load from
     * @param path the path of the resource
     */
    public ServletContextResource(ServletContext servletContext, String path) {
          // check ServletContext
          Assert.assertNotNull(servletContext, "Cannot resolve ServletContextResource without ServletContext");
          this.servletContext = servletContext;

          // check path
          Assert.assertNotNull(path, "Path is required");
          String pathToUse = FileUtils.normalizeAbsolutePath(path,true);
          if (!pathToUse.startsWith("/")) {
                pathToUse = "/" + pathToUse;
          }
          this.path = pathToUse;
    }

    /**
     * Return the ServletContext for this resource.
     */
    public final ServletContext getServletContext() {
          return this.servletContext;
    }

    /**
     * Return the path for this resource.
     */
    public final String getPath() {
          return this.path;
    }


    /**
     * This implementation checks {@code ServletContext.getResource}.
     * @see javax.servlet.ServletContext#getResource(String)
     */
    @Override
    public boolean exists() {
          try {
                URL url = this.servletContext.getResource(this.path);
                return (url != null);
          }
          catch (MalformedURLException ex) {
                return false;
          }
    }

    /**
     * This implementation delegates to {@code ServletContext.getResourceAsStream},
     * which returns {@code null} in case of a non-readable resource (e.g. a directory).
     * @see javax.servlet.ServletContext#getResourceAsStream(String)
     */
    @Override
    public boolean isReadable() {
          InputStream is = this.servletContext.getResourceAsStream(this.path);
          if (is != null) {
                try {
                      is.close();
                }
                catch (IOException ex) {
                      // ignore
                }
                return true;
          }
          else {
                return false;
          }
    }

    /**
     * This implementation delegates to {@code ServletContext.getResourceAsStream},
     * but throws a FileNotFoundException if no resource found.
     * @see javax.servlet.ServletContext#getResourceAsStream(String)
     */
    @Override
    public InputStream getInputStream() throws IOException {
          InputStream is = this.servletContext.getResourceAsStream(this.path);
          if (is == null) {
                throw new FileNotFoundException("Could not open " + getDescription());
          }
          return is;
    }

    /**
     * This implementation delegates to {@code ServletContext.getResource},
     * but throws a FileNotFoundException if no resource found.
     * @see javax.servlet.ServletContext#getResource(String)
     */
    @Override
    public URL getURL() throws IOException {
          URL url = this.servletContext.getResource(this.path);
          if (url == null) {
                throw new FileNotFoundException(
                            getDescription() + " cannot be resolved to URL because it does not exist");
          }
          return url;
    }

    /**
     * This implementation resolves "file:" URLs or alternatively delegates to
     * {@code ServletContext.getRealPath}, throwing a FileNotFoundException
     * if not found or not resolvable.
     * @see javax.servlet.ServletContext#getResource(String)
     * @see javax.servlet.ServletContext#getRealPath(String)
     */
    @Override
    public File getFile() throws IOException {
        URL url = this.servletContext.getResource(this.path);
        if (url != null && FileUtils.isFileURL(url)) {
            // Proceed with file system resolution...
            return super.getFile();
        } else {
            String tpath = path;
            if (!tpath.startsWith("/")) {
                tpath = "/" + tpath;
            }
            String realPath = servletContext.getRealPath(tpath);
            if (realPath == null) {
                throw new FileNotFoundException("ServletContext resource [" + path + "] cannot be resolved to absolute file path - "
                    + "web application archive not expanded?");
            }
            return new File(realPath);
        }
    }

    /**
     * This implementation creates a ServletContextResource, applying the given path
     * relative to the path of the underlying file of this resource descriptor.
     */
    @Override
    public ServletContextResource createRelative(String relativePath) {
          String pathToUse = FileUtils.applyRelativePath(this.path, relativePath);
          return new ServletContextResource(this.servletContext, pathToUse);
    }

    /**
     * This implementation returns the name of the file that this ServletContext
     * resource refers to.
     */
    @Override
    public String getFilename() {
          return FileUtils.getFilename(this.path);
    }

    /**
     * This implementation returns a description that includes the ServletContext
     * resource location.
     */
    @Override
    public String getDescription() {
          return "ServletContext resource [" + this.path + "]";
    }


    /**
     * This implementation compares the underlying ServletContext resource locations.
     */
    @Override
    public boolean equals(Object obj) {
          if (obj == this) {
                return true;
          }
          if (obj instanceof ServletContextResource) {
                ServletContextResource otherRes = (ServletContextResource) obj;
                return (this.servletContext.equals(otherRes.servletContext) && this.path.equals(otherRes.path));
          }
          return false;
    }

    /**
     * This implementation returns the hash code of the underlying
     * ServletContext resource location.
     */
    @Override
    public int hashCode() {
          return this.path.hashCode();
    }

}
