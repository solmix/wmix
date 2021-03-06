/**
 * Copyright (c) 2015 The Solmix Project
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solmix.commons.util.ServletUtils;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.test.TestUtils;
import org.solmix.wmix.AbstractTests;
import org.solmix.wmix.Component;
import org.solmix.wmix.Components;
import org.solmix.wmix.util.RequestURIFilter;

import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.PatchedServletRunner;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年2月1日
 */

public class WmixFilterTest extends AbstractTests
{

    private WmixFilter filter;

    private Components components;

    @Before
    public void before() throws Exception {
        prepareWebClient(null, "/myapps");
        InvocationContext icc = client.newInvocation("http://127.0.0.1/myapps/app1");
        filter = (WmixFilter) icc.getFilter();
        Assert.assertNotNull(filter);
        components = filter.getComponents();
    }

    @After
    public void tearDown() throws Exception {
        Container[] cs = ContainerFactory.getContainers();
        for (Container c : cs) {
             c.close();
        }
    }

    @Test
    public void isExcluded() throws Exception {
        filter.setExcludes("/aa , *.jpg");
        assertExcluded(true, "/aa/bb");
        assertExcluded(false, "/cc/aa/bb");
        assertExcluded(true, "/cc/bb/a.jpg");
        assertExcluded(false, "/cc/aa/bb.html");
    }

    
    private void assertExcluded(boolean excluded, String requestURI) throws Exception {
        assertExcluded(excluded, requestURI, false);
    }

    private void assertExcluded(boolean excluded, String requestURI, boolean internal) throws Exception {
        RequestURIFilter excludes = TestUtils.getFieldValue(filter, "excludeFilter", RequestURIFilter.class);

        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        FilterChain filterChain = EasyMock.createMock(FilterChain.class);

        EasyMock.expect(request.getServletPath()).andReturn(requestURI).anyTimes();
        EasyMock.expect(request.getPathInfo()).andReturn(null).anyTimes();

        if (excluded && !internal) {
            filterChain.doFilter(request, response);
        }

        EasyMock.replay(request, response, filterChain);

        if (internal) {
            Assert.assertFalse(filter.isExcluded(ServletUtils.getResourcePath(request)));
        } else {
            Assert.assertEquals(excluded, excludes.matches(requestURI));
        }

        if (excluded && !internal) {
            filter.doFilter(request, response, filterChain);
            Assert.assertTrue(filter.isExcluded(ServletUtils.getResourcePath(request)));
        }

        EasyMock.verify(request, response, filterChain);
    }
    
    @Test
    public void componentPath_wrong() throws Exception {
        File webInf = new File(TestUtils.srcdir(), "app2/WEB-INF");
        File xml = new File(webInf, "web.xml");

        try {
            new PatchedServletRunner(xml, "");
            fail();
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e, TestUtils.exception("default component \"app3\" should not have component path \"/app3\""));
        }
    }
    
    @Test
    public void getComponent() throws Exception {
       Assert.assertNull( components.getComponent("notexist"));
       Component component= components.getComponent(null);
       Assert.assertNull(component.getName());
       assertEquals("", component.getComponentPath());
    }

}
