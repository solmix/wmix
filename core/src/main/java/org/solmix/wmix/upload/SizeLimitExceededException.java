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
package org.solmix.wmix.upload;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年9月1日
 */

public class SizeLimitExceededException extends UploadException
{

    private static final long serialVersionUID = -4582907444823833303L;
    /** 创建一个异常。 */
    public SizeLimitExceededException()
    {
        super();
    }

    /**
     * 创建一个异常。
     *
     * @param message 异常信息
     */
    public SizeLimitExceededException(String message)
    {
        super(message);
    }

    /**
     * 创建一个异常。
     *
     * @param message 异常信息
     * @param cause 异常原因
     */
    public SizeLimitExceededException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * 创建一个异常。
     *
     * @param cause 异常原因
     */
    public SizeLimitExceededException(Throwable cause)
    {
        super(cause);
    }
}
