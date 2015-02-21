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

package org.solmix.wmix.web.servlet;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solmix.commons.util.ServletUtils;
import org.solmix.wmix.test.TestUtil;
import org.solmix.wmix.web.AbstractTests;
import org.solmix.wmix.web.util.RequestURIFilter;

import com.meterware.servletunit.InvocationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年2月1日
 */

public class WmixFilterTest extends AbstractTests {

    private WmixFilter filter;

    @Before
    public void before() throws Exception {
        prepareWebClient(null, "/myapps");
        InvocationContext icc = client.newInvocation("http://127.0.0.1/myapps/app1");
        filter = (WmixFilter) icc.getFilter();
        Assert.assertNotNull(filter);
    }

    @Test
    public void isExcluded() throws Exception {
        filter.setExcludes("/aa , *.jpg");
        assertExcluded(true, "/aa/bb");
    }
    private void assertExcluded(boolean excluded, String requestURI) throws Exception {
        assertExcluded(excluded, requestURI, false);
    }
    private void assertExcluded(boolean excluded, String requestURI, boolean internal) throws Exception {
        RequestURIFilter excludes = TestUtil.getFieldValue(filter, "excludeFilter", RequestURIFilter.class);

        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        FilterChain filterChain = EasyMock.createMock(FilterChain.class);

        // 不会调用getContextPath和getRequestURI
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
            filter.doFilter(request, response, filterChain); // 对excluded request调用doFilter，应该立即返回
            Assert.assertTrue(filter.isExcluded(ServletUtils.getResourcePath(request)));
        }

        EasyMock.verify(request, response, filterChain);
    }
    @Test
    public void test() {

    }
}
