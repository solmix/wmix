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

import org.apache.commons.fileupload.FileItem;
import org.solmix.wmix.upload.UploadException;
import org.solmix.wmix.upload.UploadParameters;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年8月31日
 */

public interface ParameterParser extends ValueParser
{

    /**
     * 放入Exchange参数，标示，通过servlet engine处理http参数。 值类型:Boolean
     */
    String USED_SERVLET_ENGINE = "USED_SERVLET_ENGINE_";

    /**
     * 是否使用exchange指定的encoding处理uri参数。 值类型:Boolean
     */
    String USED_EXCHANGE_ENCODING_FOR_URI = "USED_EXCHANGE_ENCODING_";

    /** 默认的编码字符集。 */
    String DEFAULT_CHARSET_ENCODING = "ISO-8859-1";

    String HTML_FIELD_SUFFIX_DEFAULT = ".~html";

    /**
     * 取得指定名称的<code>FileItem</code>对象，如果不存在，则返回<code>null</code>。
     *
     * @param key 参数名
     * @return <code>FileItem</code>对象
     */
    FileItem getFileItem(String key);

    /**
     * 添加<code>FileItem</code>。
     *
     * @param name 参数名
     * @param value 参数值
     */
    void add(String name, FileItem value);

    /**
     * 解析符合<a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>标准的 <code>multipart/form-data</code>类型的HTTP请求。
     * <p>
     * 要执行此方法，须将<code>UploadService.automatic</code>配置参数设置成<code>false</code>。
     * 此方法覆盖了service的默认设置，适合于在action或servlet中手工执行。
     * </p>
     *
     * @throws UploadException 如果解析时出错
     */
    void parseUpload() throws UploadException;

    /**
     * 解析符合<a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>标准的 <code>multipart/form-data</code>类型的HTTP请求。
     * <p>
     * 要执行此方法，须将<code>UploadService.automatic</code>配置参数设置成<code>false</code>。
     * 此方法覆盖了service的默认设置，适合于在action或servlet中手工执行。
     * </p>
     *
     * @param sizeThreshold 文件放在内存中的阈值，小于此值的文件被保存在内存中。如果此值小于0，则使用预设的值
     * @param sizeMax HTTP请求的最大尺寸，超过此尺寸的请求将被抛弃。
     * @param repositoryPath 暂存上载文件的绝对路径
     * @throws UploadException 如果解析时出错
     */
    void parseUpload(UploadParameters params) throws UploadException;

    /**
     * 将parameters重新组装成query string。
     *
     * @return query string，如果没有参数，则返回<code>null</code>
     */
    String toQueryString();
}
