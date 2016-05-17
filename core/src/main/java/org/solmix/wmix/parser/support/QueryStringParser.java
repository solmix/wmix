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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.commons.util.StringEscapeUtils;
import org.solmix.commons.util.StringUtils;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年9月1日
 */

public class QueryStringParser
{

    private final String charset;

    private final StringBuilder queryStringBuffer;

    private String equalSign = "=";

    private String andSign = "&";

    public QueryStringParser()
    {
        this(null, null);
    }

    public QueryStringParser(String charset)
    {
        this(charset, null);
    }

    public QueryStringParser(String charset, String defaultCharset)
    {
        defaultCharset = ObjectUtils.defaultIfNull(StringUtils.trimToNull(defaultCharset), Charset.defaultCharset().name());

        this.charset = ObjectUtils.defaultIfNull(StringUtils.trimToNull(charset), defaultCharset);
        this.queryStringBuffer = new StringBuilder();
    }

    public String getCharacterEncoding() {
        return charset;
    }

    public String getEqualSign() {
        return equalSign;
    }

    public String getAndSign() {
        return andSign;
    }

    /** 设置用来替代“=”的字符。 */
    public QueryStringParser setEqualSign(String equalSign) {
        this.equalSign = ObjectUtils.defaultIfNull(equalSign, "=");
        return this;
    }

    /** 设置用来替代“&”的字符。 */
    public QueryStringParser setAndSign(String andSign) {
        this.andSign = ObjectUtils.defaultIfNull(andSign, "&");
        return null;
    }

    public QueryStringParser append(String key, String value) {
        try {
            key = StringEscapeUtils.escapeURL(ObjectUtils.defaultIfNull(key, ObjectUtils.EMPTY_STRING), charset);
            value = StringEscapeUtils.escapeURL(ObjectUtils.defaultIfNull(value, ObjectUtils.EMPTY_STRING), charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("invalid charset: " + charset, e);
        }

        if (queryStringBuffer.length() > 0) {
            queryStringBuffer.append(andSign);
        }

        queryStringBuffer.append(key).append(equalSign).append(value);

        return this;
    }

    public QueryStringParser append(Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            append(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public String toQueryString() {
        String queryString = queryStringBuffer.toString();
        queryStringBuffer.setLength(0);
        return queryString.length() == 0 ? null : queryString;
    }

    /** 解析query string。 */
    public void parse(String queryString) {
        queryString = StringUtils.trimToNull(queryString);

        if (queryString == null) {
            return;
        }

        int startIndex = 0;
        int ampIndex = queryString.indexOf(andSign);

        while (ampIndex >= 0) {
            addKeyValue(queryString.substring(startIndex, ampIndex));

            startIndex = ampIndex + 1;
            ampIndex = queryString.indexOf(andSign, startIndex);
        }

        addKeyValue(queryString.substring(startIndex));
    }

    protected void add(String key, String value) {
        Assert.unsupportedOperation("You should extend class " + getClass().getSimpleName() + " and override method add(String, String)");
    }

    private void addKeyValue(String keyValue) {
        int index = keyValue.indexOf(equalSign);
        String key;
        String value;

        if (index < 0) {
            key = keyValue;
            value = null;
        } else {
            key = keyValue.substring(0, index).trim();
            value = keyValue.substring(index + 1).trim();
        }

        if (!StringUtils.isEmpty(key)) {
            key = decode(key);
            value = decode(value);

            add(key, ObjectUtils.defaultIfNull(value, ObjectUtils.EMPTY_STRING));
        }
    }

    private String decode(String str) {
        try {
            return StringEscapeUtils.unescapeURL(str, charset);
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }
}
