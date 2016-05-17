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
package org.solmix.wmix.parser;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月31日
 */

public interface CookieParser extends ValueParser
{
    int AGE_SESSION = -1;
    int AGE_DELETE  = 0;

    /** 设置session cookie。 */
    void setCookie(String name, String value);

    /**
     * Set a persisten cookie on the client that will expire after a maximum age
     * (given in seconds).
     */
    void setCookie(String name, String value, int seconds_age);

    /** Remove a previously set cookie from the client machine. */
    void removeCookie(String name);
}
