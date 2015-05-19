/*
 * Copyright 2015 The Solmix Project
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
package org.solmix.wmix.parser.support;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.wmix.parser.CookieParser;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年9月1日
 */

public class DefaultCookieParser extends AbstractValueParser implements CookieParser
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCookieParser.class);
   
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    public DefaultCookieParser(HttpServletRequest request,HttpServletResponse response){
        this.request=request;
        this.response= response;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Number of Cookies " + cookies.length);
            }

            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();

                if (LOG.isTraceEnabled()) {
                    LOG.trace("Adding " + name + " = " + value);
                }

                add(name, value);
            }
        }
    }
    @Override
    public void setCookie(String name, String value) {
        setCookie(name, value, AGE_SESSION);
    }

    
    @Override
    public void setCookie(String name, String value, int seconds_age) {
        Cookie cookie = new Cookie(name, value);

        // 设置cookie作用时间、domain和path。
        cookie.setMaxAge(seconds_age);
        cookie.setDomain(getCookieDomain());
        cookie.setPath(getCookiePath());


    }
    /**
     * 取得cookie的domain。
     *
     * @return cookie的domain
     */
    protected String getCookieDomain() {
        String domain = StringUtils.defaultIfEmpty(request.getServerName(), ObjectUtils.EMPTY_STRING);
        String[] parts = StringUtils.split(domain, ".");
        int length = parts.length;

        if (length < 2) {
            return domain;
        } else {
            // 只取最后两部分，这是最普遍的情形
            return "." + parts[length - 2] + "." + parts[length - 1];
        }
    }

    /**
     * 取得cookie的path。
     *
     * @return cookie的path
     */
    protected String getCookiePath() {
        return StringUtils.defaultIfEmpty(request.getContextPath(), "/");
    }
    @Override
    public void removeCookie(String name) {
        setCookie(name, " ", AGE_DELETE);
    }

}
