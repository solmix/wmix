package com.meterware.servletunit;

import java.io.File;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PatchedWebApplication extends WebApplication {
    private ServletContext servletContext;

    public PatchedWebApplication() {
        super();
    }

    public PatchedWebApplication(Document document, File file, String contextPath) throws MalformedURLException,
                                                                                          SAXException {
        super(document, file, contextPath);
    }

    public PatchedWebApplication(Document document, String contextPath) throws MalformedURLException, SAXException {
        super(document, contextPath);
    }

    public PatchedWebApplication(Document document) throws MalformedURLException, SAXException {
        super(document);
    }

    @Override
    public ServletContext getServletContext() {
        if (servletContext == null) {
            servletContext = new PatchedServletContext(this);
        }

        return servletContext;
    }
}
