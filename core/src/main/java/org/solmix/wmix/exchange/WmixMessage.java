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
package org.solmix.wmix.exchange;

import org.solmix.exchange.support.DefaultMessage;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月11日
 */

public class WmixMessage extends DefaultMessage
{

    private static final long serialVersionUID = -4159735280112029078L;
    public static final String HTTP_REQUEST = "HTTP.REQUEST";
    public static final String HTTP_RESPONSE = "HTTP.RESPONSE";
    public static final String RESPONSE = "response";
    public static final String REQUEST = "request";
    public static final String SESSION ="session";
    public static final String HTTP_REQUEST_METHOD="HTTP.REQUEST.METHOD";
    public static final String REQUEST_REDIRECTED = "http.request.redirected";
}
