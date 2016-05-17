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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月31日
 */

public interface UploadService extends UploadConfiguration
{
    /** 在parameters中表示upload失败，请求被忽略。 */
    String UPLOAD_FAILED = "upload_failed";
    /** 在parameters中表示upload文件尺寸超过限制值，请求被忽略。 */
    String UPLOAD_SIZE_LIMIT_EXCEEDED = "upload_size_limit_exceeded";
    boolean isMultipart(HttpServletRequest request);
    
    FileItem[] parseRequest(HttpServletRequest request);
    
    FileItem[] parseRequest(HttpServletRequest request, UploadParameters params);
}
