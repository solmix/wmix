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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ArrayUtils;
import org.solmix.commons.util.DataUtils;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.Message;
import org.solmix.wmix.parser.ParameterParser;
import org.solmix.wmix.parser.ParameterParserFilter;
import org.solmix.wmix.parser.ParameterValueFilter;
import org.solmix.wmix.parser.UploadedFileFilter;
import org.solmix.wmix.upload.SizeLimitExceededException;
import org.solmix.wmix.upload.UploadException;
import org.solmix.wmix.upload.UploadParameters;
import org.solmix.wmix.upload.UploadService;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年9月1日
 */

public class DefaultParameterParser extends AbstractValueParser implements ParameterParser
{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultParameterParser.class);

    private final UploadService upload;

    private final boolean trimming;

    private boolean uploadProcessed;

    private final ParameterParserFilter[] filters;

    private final String htmlFieldSuffix;

    private HttpServletRequest request;

    public DefaultParameterParser(Exchange ex, HttpServletRequest request, UploadService upload, ParameterParserFilter[] filter, boolean trimming,
        String htmlFieldSuffix)
    {
        this.trimming = trimming;
        this.htmlFieldSuffix = ObjectUtils.defaultIfNull(htmlFieldSuffix, HTML_FIELD_SUFFIX_DEFAULT);
        this.filters = filter;
        this.upload = upload;
        this.request = request;
        boolean isMultipart = false;
        if (upload != null) {
            isMultipart = upload.isMultipart(request);
            if (isMultipart) {
                try {
                    parseUpload();
                } catch (SizeLimitExceededException e) {
                    add(UploadService.UPLOAD_FAILED, Boolean.TRUE);
                    add(UploadService.UPLOAD_SIZE_LIMIT_EXCEEDED, Boolean.TRUE);
                    LOG.warn("File upload exceeds the size limit", e);
                } catch (UploadException e) {
                    add(UploadService.UPLOAD_FAILED, Boolean.TRUE);
                    LOG.warn("Upload failed", e);
                }
            }
        }

        if (!isMultipart) {
            String method = request.getMethod();
            boolean usedServlet = DataUtils.asBoolean(ex.get(ParameterParser.USED_SERVLET_ENGINE));
            if (usedServlet || "post".equalsIgnoreCase(method) || "put".equalsIgnoreCase(method)) {
                parseByServletEngine(request);
            } else {
                parseQueryString(ex, request);
            }
            postProcessParams();
        }
    }

    private void parseQueryString(Exchange ex, HttpServletRequest wrappedRequest) {
        // 当useBodyEncodingForURI=true时，用request.setCharacterEncoding()所指定的值来解码，否则使用URIEncoding，默认为UTF-8。
        // useBodyEncodingForURI默认值就是true。
        // 该行为和tomcat的风格一致。（不过tomcat默认是8859_1，这个没关系）
        String charset = DataUtils.asBoolean(ex.get(ParameterParser.USED_EXCHANGE_ENCODING_FOR_URI)) ? request.getCharacterEncoding()
            : (String) ex.get(Message.ENCODING);

        QueryStringParser parser = new QueryStringParser(charset, DEFAULT_CHARSET_ENCODING) {

            @Override
            protected void add(String key, String value) {
                DefaultParameterParser.this.add(key, value);
            }
        };

        parser.parse(wrappedRequest.getQueryString());
    }

    private void parseByServletEngine(HttpServletRequest wrappedRequest) {
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameters = wrappedRequest.getParameterMap();

        if (parameters != null && parameters.size() > 0) {
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                for (String value : values) {
                    add(key, value);
                }
            }
        }
    }

    @Override
    public FileItem getFileItem(String key) {
        Object o = getObject(key);
        if (o instanceof FileItem) {
            return (FileItem) o;
        } else {
            return null;
        }
    }

    @Override
    public void add(String key, FileItem value) {
        if (value.isFormField()) {
            add(key, value.getString());
        } else {
            // 忽略空的上传项。
            if (!StringUtils.isEmpty(value.getName()) || value.getSize() > 0) {
                add(key, (Object) value);
            }
        }

    }

    @Override
    public void add(String key, Object value) {
        if (value == null) {
            value = ObjectUtils.EMPTY_STRING;
        }

        if (trimming && value instanceof String) {
            value = StringUtils.trimToEmpty((String) value);
        }

        super.add(key, value);
    }

    @Override
    public void parseUpload() throws UploadException {
        parseUpload(null);
    }

    @Override
    public void parseUpload(UploadParameters params) throws UploadException {
        if (uploadProcessed || upload == null) {
            return;
        }

        FileItem[] items = upload.parseRequest(request, params);

        for (FileItem item : items) {
            add(item.getFieldName(), item);
        }

        uploadProcessed = true;

        postProcessParams();

    }

    @Override
    public String toQueryString() {
        QueryStringParser parser = new QueryStringParser();

        for (Object element : keySet()) {
            String key = (String) element;
            Object values = getObject(key);
            if (values == null || values instanceof String) {
                parser.append(key, (String) values);
            }
        }

        return parser.toQueryString();
    }

    /**
     * 处理所有参数。
     * <p>
     * 如果参数名为.~html结尾的，则按HTML规则处理，否则按普通规则处理。
     * </p>
     */
    private void postProcessParams() {
        boolean[] filtering = null;

        if (!ArrayUtils.isEmptyArray(filters)) {
            filtering = new boolean[filters.length];

            for (int i = 0; i < filters.length; i++) {
                filtering[i] = filters[i].isFiltering(request);
            }
        }

        String[] keys = getKeys();
        List<String> keysToRemove = new LinkedList<String>();

        for (String key : keys) {
            if (key.endsWith(htmlFieldSuffix)) {
                keysToRemove.add(key);
                key = key.substring(0, key.length() - htmlFieldSuffix.length());

                if (!containsKey(key)) {
                    add(key, processValues(key, true, filtering));
                }

                continue;
            }

            boolean isHtml = !StringUtils.isBlank(getString(key + htmlFieldSuffix));
            add(key, processValues(key, isHtml, filtering));
        }

        for (String key : keysToRemove) {
            remove(key);
        }
    }

    private Object processValues(String key, boolean isHtmlField, boolean[] filtering) {
        Object value = getObject(key);

        if (value instanceof String) {
            // 将非HTML字段的&#12345;转换成unicode。
            /*
             * if (!isHtmlField && isUnescapeParameters()) { value = StringEscapeUtil.unescapeEntities(null, (String)
             * value); }
             */
            // 过滤字符串值
            if (filtering != null) {
                for (int j = 0; j < filters.length; j++) {
                    ParameterParserFilter filter = filters[j];

                    if (filter instanceof ParameterValueFilter && filtering[j]) {
                        value = ((ParameterValueFilter) filter).filter(key, (String) value, isHtmlField);
                    }
                }
            }
        } else if (value instanceof FileItem) {
            // 过滤上传文件
            if (filtering != null) {
                for (int j = 0; j < filters.length; j++) {
                    ParameterParserFilter filter = filters[j];

                    if (filter instanceof UploadedFileFilter && filtering[j]) {
                        value = ((UploadedFileFilter) filter).filter(key, (FileItem) value);
                    }
                }
            }
        }

        return value;
    }
}
