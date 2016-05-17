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

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年9月1日
 */

public interface ParameterParserFilter
{

    /**
     * 是否需要过滤，如果返回否，则对于该请求的所有参数均不执行该过滤器。
     * <p>
     * 有些filter可以根据URL来确定是否要过滤参数。
     * </p>
     */
    boolean isFiltering(HttpServletRequest request);

}
